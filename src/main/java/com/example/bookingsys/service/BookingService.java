package com.example.bookingsys.service;

import com.example.bookingsys.model.AvailableClass;
import com.example.bookingsys.model.Booking;
import com.example.bookingsys.model.Waitlist;
import com.example.bookingsys.repository.BookingRepository;
import com.example.bookingsys.repository.AvailableClassRepository;
import com.example.bookingsys.repository.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookingService {

    private static final String LOCK_KEY_PREFIX = "class-lock-";

    @Autowired
    private AvailableClassRepository classRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private WaitlistRepository waitlistRepository;
    @Autowired
    private RedisLockService redisLockService;
    @Autowired
    private UserService userService;  // Handles credit deduction/refund

    @Transactional
    public Booking bookClass(Long userId, Long classId) {
        String lockKey = LOCK_KEY_PREFIX + classId;
        boolean lockAcquired = redisLockService.acquireLock(lockKey, 10, TimeUnit.SECONDS);

        if (!lockAcquired) {
            throw new RuntimeException("Could not acquire lock for booking class.");
        }

        try {
            AvailableClass classInfo = classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            // Check if the class is full
            if (classInfo.getCurrentBookedSlots() >= classInfo.getTotalSlots()) {
                // Add to waitlist if the class is full
                waitlistRepository.save(new Waitlist(userId, classId, LocalDateTime.now()));
                return null; // Return null or response indicating waitlisted status
            }

            // Deduct user credits
            if (!userService.deductCredits(userId, classInfo.getRequiredCredits())) {
                throw new RuntimeException("Insufficient credits for booking.");
            }

            // Proceed with booking
            classInfo.setCurrentBookedSlots(classInfo.getCurrentBookedSlots() + 1);
            classRepository.save(classInfo);

            Booking booking = new Booking(userId, classId, LocalDateTime.now(), true, classInfo.getRequiredCredits(), true);
            return bookingRepository.save(booking);

        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        AvailableClass classInfo = classRepository.findById(booking.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (classInfo.getStartTime().isAfter(LocalDateTime.now().plusHours(4))) {
            // Refund user credits if canceled at least 4 hours before class time
            userService.refundCredits(booking.getUserId(), booking.getCreditsUsed());
        }

        booking.setActive(false);
        classInfo.setCurrentBookedSlots(classInfo.getCurrentBookedSlots() - 1);
        bookingRepository.save(booking);
        classRepository.save(classInfo);

        // Move first waitlisted user to booked status (FIFO)
        List<Waitlist> waitlist = waitlistRepository.findByClassIdOrderByAddedTimeAsc(classInfo.getId());
        if (!waitlist.isEmpty()) {
            Waitlist waitlistedUser = waitlist.remove(0);
            bookClass(waitlistedUser.getUserId(), classInfo.getId());
            waitlistRepository.delete(waitlistedUser);
        }
    }

    public void handleEndOfClass(Long classId) {
        List<Waitlist> waitlistedUsers = waitlistRepository.findByClassId(classId);

        for (Waitlist waitlist : waitlistedUsers) {
            // Refund credits to each waitlisted user who didn’t get a booking
            userService.refundCredits(waitlist.getUserId(), classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found")).getRequiredCredits());
        }

        // Clear the waitlist for the class after refunds
        waitlistRepository.deleteAll(waitlistedUsers);
    }
}

