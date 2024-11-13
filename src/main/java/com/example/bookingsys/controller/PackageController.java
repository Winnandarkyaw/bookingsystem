package com.example.bookingsys.controller;

import com.example.bookingsys.model.Package;
import com.example.bookingsys.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    // Endpoint to get available packages for a specific country
    @GetMapping("/available")
    public ResponseEntity<List<Package>> getAvailablePackages(@RequestParam String countryCode) {
        List<Package> packages = packageService.getAvailablePackages(countryCode);
        return ResponseEntity.ok(packages);
    }

    // Endpoint to get a user's purchased packages
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Package>> getUserPackages(@PathVariable Long userId) {
        List<Package> packages = packageService.getUserPackages(userId);
        return ResponseEntity.ok(packages);
    }

    // Endpoint to check the remaining credits of a user
    @GetMapping("/user/{userId}/credits")
    public ResponseEntity<Integer> getRemainingCredits(@PathVariable Long userId) {
        int remainingCredits = packageService.getRemainingCredits(userId);
        return ResponseEntity.ok(remainingCredits);
    }

    // Endpoint to purchase a new package for a user
    @PostMapping("/purchase")
    public ResponseEntity<Package> purchasePackage(
            @RequestParam Long userId,
            @RequestParam String countryCode,
            @RequestParam int credits,
            @RequestParam String validUntil) {

        LocalDateTime validUntilDateTime = LocalDateTime.parse(validUntil);
        try {
            Package newPackage = packageService.purchasePackage(userId, countryCode, credits, validUntilDateTime);
            return new ResponseEntity<>(newPackage, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint to refund credits to a user
    @PostMapping("/refund")
    public ResponseEntity<String> refundCredits(
            @RequestParam Long userId,
            @RequestParam int credits) {

        try {
            packageService.refundCredits(userId, credits);
            return ResponseEntity.ok("Refund successful.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refund failed.");
        }
    }

    // Endpoint to handle package expiration (expire outdated packages)
    @PostMapping("/expire")
    public ResponseEntity<String> expirePackages() {
        packageService.expirePackages();
        return ResponseEntity.ok("Expired packages processed.");
    }
}
