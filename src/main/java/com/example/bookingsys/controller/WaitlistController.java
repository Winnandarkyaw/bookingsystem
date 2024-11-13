package com.example.bookingsys.controller;


import com.example.bookingsys.model.Waitlist;
import com.example.bookingsys.service.WaitlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/waitlist")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    // Add a user to the waitlist for a given class
    @PostMapping("/add")
    public ResponseEntity<String> addToWaitlist(@RequestParam Long userId, @RequestParam Long classId, @RequestParam int maxWaitlistSize) {
        boolean added = waitlistService.addToWaitlist(userId, classId, maxWaitlistSize);
        if (added) {
            return new ResponseEntity<>("User added to the waitlist.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("The waitlist is full.", HttpStatus.BAD_REQUEST);
        }
    }

    // Get the user's position in the waitlist for a specific class
    @GetMapping("/position")
    public ResponseEntity<String> getWaitlistPosition(@RequestParam Long userId, @RequestParam Long classId) {
        int position = waitlistService.getWaitlistPosition(userId, classId);
        if (position > 0) {
            return new ResponseEntity<>("User is in position " + position + " on the waitlist.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not on the waitlist.", HttpStatus.NOT_FOUND);
        }
    }

    // Promote the first user from the waitlist to a confirmed booking
    @PostMapping("/promote")
    public ResponseEntity<String> promoteFromWaitlist(@RequestParam Long classId) {
        Optional<Waitlist> promotedUser = waitlistService.promoteFromWaitlist(classId);
        if (promotedUser.isPresent()) {
            return new ResponseEntity<>("User promoted from the waitlist.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Waitlist is empty for this class.", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to manually expire entries (optional if handled by scheduled task)
    @DeleteMapping("/expire")
    public ResponseEntity<String> expireWaitlistEntries() {
        waitlistService.removeExpiredWaitlistEntries();
        return new ResponseEntity<>("Expired waitlist entries removed.", HttpStatus.OK);
    }

    // Notify users who have been on the waitlist for a specific time
    @PostMapping("/notify")
    public ResponseEntity<String> notifyWaitlistReminders() {
        waitlistService.sendWaitlistReminders();
        return new ResponseEntity<>("Reminders sent to users on the waitlist.", HttpStatus.OK);
    }
}
