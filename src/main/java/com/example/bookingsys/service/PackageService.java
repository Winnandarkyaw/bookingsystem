package com.example.bookingsys.service;

import com.example.bookingsys.model.Package;
import com.example.bookingsys.model.User;
import com.example.bookingsys.repository.PackageRepository;
import com.example.bookingsys.repository.BookingRepository;
import com.example.bookingsys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PackageRepository packageRepository;


    @Autowired
    private MockService mockServices; // Inject the MockServices


    @Autowired
    private BookingRepository bookingRepository;

    // Fetch available packages for a specific country
    public List<Package> getAvailablePackages(String countryCode) {
        return packageRepository.findByCountryCode(countryCode);
    }



    // Fetch user's purchased packages
    public List<Package> getUserPackages(Long userId) {
        return packageRepository.findByUserId(userId);
    }

    public int getRemainingCredits(Long userId) {
        List<Package> userPackages = getUserPackages(userId);
        return userPackages.stream()
                .filter(pkg -> pkg.getValidUntil().isAfter(LocalDateTime.now()))
                .mapToInt(Package::getCredits)
                .sum();
    }
    // Get expired packages for a user
    public List<Package> getExpiredPackages(Long userId) {
        List<Package> userPackages = getUserPackages(userId);
        return userPackages.stream()
                .filter(pkg -> pkg.getValidUntil().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    // Check if a user has enough credits to book a class
    public boolean hasEnoughCredits(Long userId, int requiredCredits) {
        int remainingCredits = getRemainingCredits(userId);
        return remainingCredits >= requiredCredits;
    }

    @Transactional
    public Package purchasePackage(Long userId, String countryCode, int credits, LocalDateTime validUntil) {
        // Log the payment attempt
        logger.info("Attempting to purchase package for user {} with {} credits.", userId, credits);

        // Fetch user by userId
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Use MockServices to simulate the payment process
        if (mockServices.paymentCharge(credits)) {  // Call the paymentCharge method from MockServices
            // If payment is successful, create a new package and associate the user with it
            Package pkg = new Package(user, countryCode, credits, validUntil);  // Use the existing user object

            // Save the package to the database
            Package savedPackage = packageRepository.save(pkg);

            // Log the successful purchase
            logger.info("Package purchased successfully for user {} with {} credits.", userId, credits);
            return savedPackage;
        } else {
            // Log the failure and throw an exception
            logger.warn("Payment failed for user {}.", userId);
            throw new IllegalStateException("Payment failed.");
        }
    }

    // Refund credits when a class booking is canceled
    @Transactional
    public void refundCredits(Long userId, int credits) {
        // Log the refund attempt
        logger.info("Refunding {} credits to user {}.", credits, userId);

        List<Package> userPackages = getUserPackages(userId);
        if (userPackages.isEmpty()) {
            logger.warn("User {} has no valid packages to refund.", userId);
            throw new IllegalStateException("User has no valid packages to refund.");
        }

        for (Package pkg : userPackages) {
            if (pkg.getValidUntil().isAfter(LocalDateTime.now())) {
                // Refund credits to the user's valid package
                pkg.setCredits(pkg.getCredits() + credits);
                packageRepository.save(pkg);
                logger.info("Refunded {} credits to user {} for package ID {}.", credits, userId, pkg.getId());
                return;
            }
        }

        logger.warn("No valid packages found for user {} to refund credits.", userId);
        throw new IllegalStateException("No valid packages to refund.");
    }

    // Handle package expiration (expire packages that are past their valid date)
    @Transactional
    public void expirePackages() {
        List<Package> allPackages = packageRepository.findAll();
        for (Package pkg : allPackages) {
            if (pkg.getValidUntil().isBefore(LocalDateTime.now())) {
                pkg.setExpired(true);
                packageRepository.save(pkg);
                logger.info("Package ID {} has expired and is marked as expired.", pkg.getId());
            }
        }
    }

    // Simulate a payment process (mock function)
    private boolean processPayment(int credits) {
        // Log the mock payment processing
        logger.info("Processing payment for {} credits.", credits);
        // Simulate a successful payment
        return true;
    }
}
