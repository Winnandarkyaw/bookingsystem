package com.example.bookingsys.service;


import com.example.bookingsys.model.AvailableClass;
import com.example.bookingsys.repository.AvailableClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvailableClassService {

    @Autowired
    private AvailableClassRepository availableClassRepository;

    @Autowired
    private WaitlistService waitlistService;


    // Retrieve all available classes
    public List<AvailableClass> getAllClasses() {
        return availableClassRepository.findAll();
    }

    // Retrieve an available class by ID
    public Optional<AvailableClass> getClassById(Long id) {
        return availableClassRepository.findById(id);
    }

    // Create a new available class
    public AvailableClass createClass(AvailableClass availableClass) {
        return availableClassRepository.save(availableClass);
    }

    // Update an existing class
    public AvailableClass updateClass(Long id, AvailableClass updatedClass) {
        return availableClassRepository.findById(id).map(existingClass -> {
            existingClass.setName(updatedClass.getName());
            existingClass.setCountryCode(updatedClass.getCountryCode());
            existingClass.setRequiredCredits(updatedClass.getRequiredCredits());
            existingClass.setTotalSlots(updatedClass.getTotalSlots());
            existingClass.setStartTime(updatedClass.getStartTime());
            existingClass.setEndTime(updatedClass.getEndTime());
            existingClass.setCurrentBookedSlots(updatedClass.getCurrentBookedSlots());
            return availableClassRepository.save(existingClass);
        }).orElseThrow(() -> new RuntimeException("Class not found with id " + id));
    }

    // Delete a class by ID
    public void deleteClass(Long id) {
        availableClassRepository.deleteById(id);
    }

    // Check if slots are available for booking
    public boolean hasAvailableSlots(Long classId) {
        Optional<AvailableClass> availableClass = availableClassRepository.findById(classId);
        return availableClass.map(cls -> cls.getCurrentBookedSlots() < cls.getTotalSlots()).orElse(false);
    }

    // Increment booked slots count
    public AvailableClass incrementBookedSlots(Long classId) {
        return availableClassRepository.findById(classId).map(cls -> {
            cls.setCurrentBookedSlots(cls.getCurrentBookedSlots() + 1);
            return availableClassRepository.save(cls);
        }).orElseThrow(() -> new RuntimeException("Class not found with id " + classId));
    }


    // Check if the class is full
    public boolean isClassFull(Long classId) {
        Optional<AvailableClass> availableClass = availableClassRepository.findById(classId);
        return availableClass.map(cls -> cls.getCurrentBookedSlots() >= cls.getTotalSlots()).orElse(false);
    }

    // Mark expired classes as expired
    public void expireClasses() {
        LocalDateTime now = LocalDateTime.now();
        List<AvailableClass> allClasses = availableClassRepository.findAll();

        for (AvailableClass availableClass : allClasses) {
            if (availableClass.getEndTime().isBefore(now)) {
                // Mark the class as expired (you could also delete, or use some other flag)
                availableClass.setEndTime(now);  // or set some expired flag
                availableClassRepository.save(availableClass);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")  // Runs every midnight
    public void scheduledExpireClasses() {
        expireClasses();
    }

    // Increase the capacity of a class
    public AvailableClass increaseClassCapacity(Long classId, int additionalSlots) {
        Optional<AvailableClass> availableClass = availableClassRepository.findById(classId);
        if (availableClass.isPresent()) {
            AvailableClass cls = availableClass.get();
            if (cls.getCurrentBookedSlots() + additionalSlots <= cls.getTotalSlots()) {
                cls.setTotalSlots(cls.getTotalSlots() + additionalSlots);
                return availableClassRepository.save(cls);
            } else {
                throw new RuntimeException("Cannot increase capacity beyond current bookings.");
            }
        } else {
            throw new RuntimeException("Class not found.");
        }
    }


    public void addToWaitlistIfClassFull(Long classId, Long userId) {
        if (isClassFull(classId)) {
            waitlistService.addToWaitlist(classId, userId);
        } else {
            // Proceed with booking as usual
        }
    }

    // Notify when class is near full
    public void checkClassCapacityAndNotify() {
        List<AvailableClass> allClasses = availableClassRepository.findAll();
        for (AvailableClass availableClass : allClasses) {
            if (availableClass.getTotalSlots() - availableClass.getCurrentBookedSlots() <= 5) {
                // Send notification (via email, logging, etc.)
                System.out.println("Class " + availableClass.getName() + " is near full.");
            }
        }
    }
}
