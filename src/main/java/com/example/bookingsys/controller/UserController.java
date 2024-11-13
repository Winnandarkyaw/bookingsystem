package com.example.bookingsys.controller;

import com.example.bookingsys.model.User;
import com.example.bookingsys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user registration and authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user", description = "Registers a new user by providing their details.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Parameter(description = "User object containing registration details", required = true)
            @RequestBody User user) {

        return userService.registerUser(user);
    }

    @Operation(summary = "User login", description = "Authenticates a user with a username and password.")
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @Parameter(description = "Username of the user", required = true)
            @RequestParam String username,

            @Parameter(description = "Password of the user", required = true)
            @RequestParam String password) {

        return userService.authenticateUser(username, password);
    }
}
