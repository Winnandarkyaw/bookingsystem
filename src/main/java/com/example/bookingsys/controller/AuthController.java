package com.example.bookingsys.controller;

import com.example.bookingsys.model.User;
import com.example.bookingsys.service.UserService;
import com.example.bookingsys.service.QuartzSchedulerService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    // Endpoint for registering a new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        ResponseEntity<String> registrationResponse = userService.registerUser(user);
        if (!registrationResponse.getStatusCode().is2xxSuccessful()) {
            return registrationResponse;
        }

        // Schedule email verification job
        try {
            quartzSchedulerService.scheduleEmailVerificationJob(user.getUsername());
            return ResponseEntity.ok("User registered successfully, verification email scheduled.");
        } catch (SchedulerException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("User registered, but error scheduling email verification.");
        }
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        return userService.authenticateUser(username, password);
    }

    // Endpoint to get user profile by username
    @GetMapping("/profile/{username}")
    public ResponseEntity<User> getProfile(@PathVariable String username) {
        User user = userService.getUserProfile(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(404).body(null);
    }

    // Endpoint for resetting user password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        if (userService.resetPassword(username, newPassword)) {
            return ResponseEntity.ok("Password reset successfully.");
        }
        return ResponseEntity.status(404).body("User not found.");
    }
}
