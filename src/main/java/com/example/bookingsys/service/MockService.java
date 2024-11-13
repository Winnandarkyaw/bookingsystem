package com.example.bookingsys.service;


import org.springframework.stereotype.Service;

@Service
public class MockService{

    // Simulate adding a payment card
    public boolean addPaymentCard(String cardNumber, String cardHolder) {
        // Simulated logic for adding a payment card
        System.out.println("Payment card added: " + cardHolder + " - " + cardNumber);
        return true;
    }

    // Simulate processing a payment charge
    public boolean paymentCharge(int credits) {
        // Simulate a random 50% chance of success/failure
        if (Math.random() > 0.5) {
            System.out.println("Payment successful for " + credits + " credits.");
            return true;
        } else {
            System.out.println("Payment failed for " + credits + " credits.");
            return false;
        }
    }

    // Simulate sending a verification email
    public boolean sendVerifyEmail(String email) {
        // Simulated email sending logic
        System.out.println("Verification email sent to: " + email);
        return true;
    }
}
