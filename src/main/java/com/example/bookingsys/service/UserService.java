package com.example.bookingsys.service;

import com.example.bookingsys.model.User;
import com.example.bookingsys.repository.UserRepository;
import com.example.bookingsys.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Registration with email verification
    public ResponseEntity<String> registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false); // Initially set email as not verified
        userRepository.save(user);

        // Mock sending a verification email
        if (SendVerifyEmail(user.getEmail())) {
            return ResponseEntity.ok("User registered successfully. Verification email sent.");
        } else {
            return ResponseEntity.status(500).body("User registered, but failed to send verification email.");
        }
    }

    // Authentication
    public ResponseEntity<String> authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        String jwt = jwtUtil.generateToken(username);
        return ResponseEntity.ok("Bearer " + jwt);
    }

    // Retrieve user profile by username
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Reset password for a user
    public boolean resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Mock function to simulate sending a verification email
    public boolean SendVerifyEmail(String email) {
        System.out.println("Verification email sent to: " + email);
        return true; // Simulate successful email send
    }

    // Mock function to simulate adding payment card
    public boolean AddPaymentCard(String cardDetails) {
        System.out.println("Payment card added: " + cardDetails);
        return true;
    }

    // Mock function to simulate charging payment
    public boolean PaymentCharge(double amount) {
        System.out.println("Payment charged: $" + amount);
        return true;
    }
}
