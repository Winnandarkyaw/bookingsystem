package com.example.bookingsys.controller;

import com.example.bookingsys.model.Package;
import com.example.bookingsys.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package", description = "API for managing packages and credits")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Operation(summary = "Get available packages for a specific country", description = "Retrieves a list of packages available in a given country by its country code.")
    @GetMapping("/available")
    public ResponseEntity<List<Package>> getAvailablePackages(
            @Parameter(description = "Country code for the available packages", required = true) @RequestParam String countryCode) {

        List<Package> packages = packageService.getAvailablePackages(countryCode);
        return ResponseEntity.ok(packages);
    }

    @Operation(summary = "Get purchased packages for a user", description = "Retrieves all the packages purchased by a specific user.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Package>> getUserPackages(
            @Parameter(description = "User ID to retrieve their purchased packages", required = true) @PathVariable Long userId) {

        List<Package> packages = packageService.getUserPackages(userId);
        return ResponseEntity.ok(packages);
    }

    @Operation(summary = "Get remaining credits for a user", description = "Retrieves the remaining credits of a specific user.")
    @GetMapping("/user/{userId}/credits")
    public ResponseEntity<Integer> getRemainingCredits(
            @Parameter(description = "User ID to retrieve the remaining credits", required = true) @PathVariable Long userId) {

        int remainingCredits = packageService.getRemainingCredits(userId);
        return ResponseEntity.ok(remainingCredits);
    }

    @Operation(summary = "Purchase a new package", description = "Allows a user to purchase a new package by specifying userId, country code, credits, and validity date.")
    @PostMapping("/purchase")
    public ResponseEntity<Package> purchasePackage(
            @Parameter(description = "User ID making the purchase", required = true) @RequestParam Long userId,
            @Parameter(description = "Country code for the package purchase", required = true) @RequestParam String countryCode,
            @Parameter(description = "Credits for the new package", required = true) @RequestParam int credits,
            @Parameter(description = "Validity date of the package in ISO-8601 format", required = true) @RequestParam String validUntil) {

        LocalDateTime validUntilDateTime = LocalDateTime.parse(validUntil);
        try {
            Package newPackage = packageService.purchasePackage(userId, countryCode, credits, validUntilDateTime);
            return new ResponseEntity<>(newPackage, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Refund credits to a user", description = "Refunds a specified number of credits to a user.")
    @PostMapping("/refund")
    public ResponseEntity<String> refundCredits(
            @Parameter(description = "User ID to refund credits", required = true) @RequestParam Long userId,
            @Parameter(description = "Credits to be refunded", required = true) @RequestParam int credits) {

        try {
            packageService.refundCredits(userId, credits);
            return ResponseEntity.ok("Refund successful.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refund failed.");
        }
    }

    @Operation(summary = "Expire outdated packages", description = "Expires packages that are outdated or no longer valid.")
    @PostMapping("/expire")
    public ResponseEntity<String> expirePackages() {
        packageService.expirePackages();
        return ResponseEntity.ok("Expired packages processed.");
    }
}
