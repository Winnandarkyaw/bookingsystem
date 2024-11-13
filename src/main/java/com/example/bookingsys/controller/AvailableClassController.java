package com.example.bookingsys.controller;

import com.example.bookingsys.model.AvailableClass;
import com.example.bookingsys.service.AvailableClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all available classes", description = "Returns a list of all available classes.")
    @GetMapping
    public ResponseEntity<List<AvailableClass>> getAllClasses() {
        List<AvailableClass> classes = availableClassService.getAllClasses();
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @Operation(summary = "Get a class by ID", description = "Fetches a specific available class by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved class"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AvailableClass> getClassById(@Parameter(description = "ID of the class to be retrieved") @PathVariable Long id) {
        Optional<AvailableClass> availableClass = availableClassService.getClassById(id);
        return availableClass.map(cls -> new ResponseEntity<>(cls, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Create a new class", description = "Creates a new available class.")
    @PostMapping
    public ResponseEntity<AvailableClass> createClass(@RequestBody AvailableClass availableClass) {
        AvailableClass newClass = availableClassService.createClass(availableClass);
        return new ResponseEntity<>(newClass, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing class", description = "Updates the details of an existing class by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<AvailableClass> updateClass(@Parameter(description = "ID of the class to be updated") @PathVariable Long id, @RequestBody AvailableClass updatedClass) {
        try {
            AvailableClass updated = availableClassService.updateClass(id, updatedClass);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a class", description = "Deletes an existing class by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@Parameter(description = "ID of the class to be deleted") @PathVariable Long id) {
        availableClassService.deleteClass(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Check if slots are available for booking", description = "Checks if there are available slots for a specific class.")
    @GetMapping("/{id}/hasAvailableSlots")
    public ResponseEntity<Boolean> hasAvailableSlots(@PathVariable Long id) {
        boolean available = availableClassService.hasAvailableSlots(id);
        return new ResponseEntity<>(available, HttpStatus.OK);
    }

    @Operation(summary = "Increment booked slots count", description = "Increments the number of booked slots for a class.")
    @PostMapping("/{id}/incrementBookedSlots")
    public ResponseEntity<AvailableClass> incrementBookedSlots(@PathVariable Long id) {
        try {
            AvailableClass updatedClass = availableClassService.incrementBookedSlots(id);
            return new ResponseEntity<>(updatedClass, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Check if class is full", description = "Checks if the class has reached its capacity.")
    @GetMapping("/{id}/isFull")
    public ResponseEntity<Boolean> isClassFull(@PathVariable Long id) {
        boolean isFull = availableClassService.isClassFull(id);
        return new ResponseEntity<>(isFull, HttpStatus.OK);
    }

    @Operation(summary = "Check if class has expired", description = "Checks if the class has expired based on its end time.")
    @GetMapping("/{id}/isExpired")
    public ResponseEntity<Boolean> isClassExpired(@PathVariable Long id) {
        Optional<AvailableClass> availableClass = availableClassService.getClassById(id);
        if (availableClass.isPresent()) {
            boolean isExpired = availableClass.get().getEndTime().isBefore(LocalDateTime.now());
            return new ResponseEntity<>(isExpired, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Increase class capacity", description = "Increases the capacity of a class by the specified number of additional slots.")
    @PutMapping("/{id}/increaseCapacity")
    public ResponseEntity<AvailableClass> increaseClassCapacity(@PathVariable Long id, @RequestParam int additionalSlots) {
        try {
            AvailableClass updatedClass = availableClassService.increaseClassCapacity(id, additionalSlots);
            return new ResponseEntity<>(updatedClass, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Add user to the waitlist if class is full", description = "Adds a user to the waitlist if the class is full.")
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
