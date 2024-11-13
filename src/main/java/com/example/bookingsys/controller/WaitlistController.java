package com.example.bookingsys.controller;

import com.example.bookingsys.model.Waitlist;
import com.example.bookingsys.service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/waitlist")
@Tag(name = "Waitlist Management", description = "API for managing waitlist for classes")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    @Operation(summary = "Add a user to the waitlist for a specific class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added to the waitlist"),
            @ApiResponse(responseCode = "400", description = "The waitlist is full")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addToWaitlist(
            @Parameter(description = "User ID of the person to add to the waitlist", required = true) @RequestParam Long userId,
            @Parameter(description = "Class ID for the waitlist", required = true) @RequestParam Long classId,
            @Parameter(description = "Maximum size of the waitlist", required = true) @RequestParam int maxWaitlistSize) {

        boolean added = waitlistService.addToWaitlist(userId, classId, maxWaitlistSize);
        if (added) {
            return new ResponseEntity<>("User added to the waitlist.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("The waitlist is full.", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get the user's position on the waitlist for a specific class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User position on the waitlist"),
            @ApiResponse(responseCode = "404", description = "User not on the waitlist")
    })
    @GetMapping("/position")
    public ResponseEntity<String> getWaitlistPosition(
            @Parameter(description = "User ID to check position for", required = true) @RequestParam Long userId,
            @Parameter(description = "Class ID to check position in waitlist", required = true) @RequestParam Long classId) {

        int position = waitlistService.getWaitlistPosition(userId, classId);
        if (position > 0) {
            return new ResponseEntity<>("User is in position " + position + " on the waitlist.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not on the waitlist.", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Promote the first user from the waitlist to a confirmed booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User promoted from the waitlist"),
            @ApiResponse(responseCode = "404", description = "Waitlist is empty for this class")
    })
    @PostMapping("/promote")
    public ResponseEntity<String> promoteFromWaitlist(
            @Parameter(description = "Class ID for which to promote a user", required = true) @RequestParam Long classId) {

        Optional<Waitlist> promotedUser = waitlistService.promoteFromWaitlist(classId);
        if (promotedUser.isPresent()) {
            return new ResponseEntity<>("User promoted from the waitlist.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Waitlist is empty for this class.", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Expire outdated waitlist entries")
    @ApiResponse(responseCode = "200", description = "Expired waitlist entries removed")
    @DeleteMapping("/expire")
    public ResponseEntity<String> expireWaitlistEntries() {
        waitlistService.removeExpiredWaitlistEntries();
        return new ResponseEntity<>("Expired waitlist entries removed.", HttpStatus.OK);
    }

    @Operation(summary = "Send reminders to users on the waitlist")
    @ApiResponse(responseCode = "200", description = "Reminders sent to users on the waitlist")
    @PostMapping("/notify")
    public ResponseEntity<String> notifyWaitlistReminders() {
        waitlistService.sendWaitlistReminders();
        return new ResponseEntity<>("Reminders sent to users on the waitlist.", HttpStatus.OK);
    }
}
