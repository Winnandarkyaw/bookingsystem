package com.example.bookingsys.controller;


import com.example.bookingsys.model.AvailableClass;
import com.example.bookingsys.service.AvailableClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
public class AvailableClassController {

    private final AvailableClassService availableClassService;

    @Autowired
    public AvailableClassController(AvailableClassService availableClassService) {
        this.availableClassService = availableClassService;
    }

    // Get all available classes
    @GetMapping
    public ResponseEntity<List<AvailableClass>> getAllClasses() {
        List<AvailableClass> classes = availableClassService.getAllClasses();
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    // Get a class by ID
    @GetMapping("/{id}")
    public ResponseEntity<AvailableClass> getClassById(@PathVariable Long id) {
        Optional<AvailableClass> availableClass = availableClassService.getClassById(id);
        return availableClass.map(cls -> new ResponseEntity<>(cls, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Create a new class
    @PostMapping
    public ResponseEntity<AvailableClass> createClass(@RequestBody AvailableClass availableClass) {
        AvailableClass newClass = availableClassService.createClass(availableClass);
        return new ResponseEntity<>(newClass, HttpStatus.CREATED);
    }

    // Update an existing class
    @PutMapping("/{id}")
    public ResponseEntity<AvailableClass> updateClass(@PathVariable Long id, @RequestBody AvailableClass updatedClass) {
        try {
            AvailableClass updated = availableClassService.updateClass(id, updatedClass);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a class
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        availableClassService.deleteClass(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Check if slots are available for booking
    @GetMapping("/{id}/hasAvailableSlots")
    public ResponseEntity<Boolean> hasAvailableSlots(@PathVariable Long id) {
        boolean available = availableClassService.hasAvailableSlots(id);
        return new ResponseEntity<>(available, HttpStatus.OK);
    }

    // Increment booked slots count
    @PostMapping("/{id}/incrementBookedSlots")
    public ResponseEntity<AvailableClass> incrementBookedSlots(@PathVariable Long id) {
        try {
            AvailableClass updatedClass = availableClassService.incrementBookedSlots(id);
            return new ResponseEntity<>(updatedClass, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to check if a class is full
    @GetMapping("/{id}/isFull")
    public ResponseEntity<Boolean> isClassFull(@PathVariable Long id) {
        boolean isFull = availableClassService.isClassFull(id);
        return new ResponseEntity<>(isFull, HttpStatus.OK);
    }

    @GetMapping("/{id}/isExpired")
    public ResponseEntity<Boolean> isClassExpired(@PathVariable Long id) {
        Optional<AvailableClass> availableClass = availableClassService.getClassById(id);
        if (availableClass.isPresent()) {
            boolean isExpired = availableClass.get().getEndTime().isBefore(LocalDateTime.now());
            return new ResponseEntity<>(isExpired, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/increaseCapacity")
    public ResponseEntity<AvailableClass> increaseClassCapacity(@PathVariable Long id, @RequestParam int additionalSlots) {
        try {
            AvailableClass updatedClass = availableClassService.increaseClassCapacity(id, additionalSlots);
            return new ResponseEntity<>(updatedClass, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Add user to the waitlist if class is full
    @PostMapping("/{id}/waitlist/{userId}")
    public ResponseEntity<String> addToWaitlist(@PathVariable Long id, @PathVariable Long userId) {
        try {
            availableClassService.addToWaitlistIfClassFull(id, userId);
            return new ResponseEntity<>("Added to waitlist.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Class is not full, booking is allowed.", HttpStatus.OK);
        }
    }
}
