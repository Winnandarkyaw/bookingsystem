package com.example.bookingsys.controller;

import com.example.bookingsys.model.Booking;
import com.example.bookingsys.service.BookingService;
import com.example.bookingsys.dto.ApiResponse;
import com.example.bookingsys.exception.ClassFullException;
import com.example.bookingsys.exception.InsufficientCreditsException;
import com.example.bookingsys.exception.BookingNotFoundException;
import com.example.bookingsys.exception.GenericException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking", description = "API for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Book a class", description = "Allows a user to book a class with a userId and classId.")
    @PostMapping("/book")
    public ApiResponse<Booking> bookClass(
            @Parameter(description = "User ID", required = true) @RequestParam Long userId,
            @Parameter(description = "Class ID", required = true) @RequestParam Long classId) {

        try {
            Booking booking = bookingService.bookClass(userId, classId);

            if (booking == null) {
                return new ApiResponse<>(HttpStatus.OK, "Class is full, added to the waitlist.", null);
            }

            return new ApiResponse<>(HttpStatus.CREATED, "Class booked successfully.", booking);
        } catch (ClassFullException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Class is full. Added to the waitlist.", null);
        } catch (InsufficientCreditsException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Insufficient credits to book the class.", null);
        } catch (GenericException e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while booking the class.", null);
        }
    }

    @Operation(summary = "Cancel a booking", description = "Allows a user to cancel an existing booking by bookingId.")
    @PostMapping("/cancel/{bookingId}")
    public ApiResponse<String> cancelBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long bookingId) {

        try {
            bookingService.cancelBooking(bookingId);
            return new ApiResponse<>(HttpStatus.OK, "Booking canceled successfully.", "Booking ID: " + bookingId);
        } catch (BookingNotFoundException e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, "Booking not found.", null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while canceling the booking.", null);
        }
    }

    @Operation(summary = "Handle end of class", description = "Handles the end of class and processes waitlisted users.")
    @PostMapping("/end/{classId}")
    public ApiResponse<String> handleEndOfClass(
            @Parameter(description = "Class ID", required = true) @PathVariable Long classId) {

        try {
            bookingService.handleEndOfClass(classId);
            return new ApiResponse<>(HttpStatus.OK, "Handled end of class and processed waitlisted users.", null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the end of class.", null);
        }
    }

    @Operation(summary = "Get bookings for a user", description = "Retrieves all bookings for a user by userId.")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Booking>> getBookingsForUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {

        try {
            List<Booking> bookings = bookingService.getBookingsForUser(userId);

            if (bookings.isEmpty()) {
                return new ApiResponse<>(HttpStatus.OK, "No bookings found for the user.", null);
            }

            return new ApiResponse<>(HttpStatus.OK, "Bookings retrieved successfully.", bookings);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving bookings.", null);
        }
    }

    @Operation(summary = "Get booking by ID", description = "Retrieve a booking by its bookingId.")
    @GetMapping("/{bookingId}")
    public ApiResponse<Booking> getBookingById(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long bookingId) {

        try {
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND, "Booking not found.", null);
            }
            return new ApiResponse<>(HttpStatus.OK, "Booking retrieved successfully.", booking);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving the booking.", null);
        }
    }

    @Operation(summary = "Update booking", description = "Update booking details by bookingId.")
    @PutMapping("/{bookingId}")
    public ApiResponse<Booking> updateBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long bookingId,
            @Parameter(description = "Updated Booking Details", required = true) @RequestBody Booking updatedBooking) {

        try {
            Booking updated = bookingService.updateBooking(bookingId, updatedBooking);

            if (updated == null) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND, "Booking not found.", null);
            }

            return new ApiResponse<>(HttpStatus.OK, "Booking updated successfully.", updated);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the booking.", null);
        }
    }

    @Operation(summary = "Confirm booking", description = "Confirm a booking by bookingId.")
    @PostMapping("/confirm/{bookingId}")
    public ApiResponse<String> confirmBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long bookingId) {

        try {
            bookingService.confirmBooking(bookingId);
            return new ApiResponse<>(HttpStatus.OK, "Booking confirmed successfully.", "Booking ID: " + bookingId);
        } catch (BookingNotFoundException e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, "Booking not found.", null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while confirming the booking.", null);
        }
    }

    @Operation(summary = "Get waitlist for a class", description = "Retrieve the list of users on the waitlist for a specific class.")
    @GetMapping("/waitlist/{classId}")
    public ApiResponse<List<Booking>> getWaitlistForClass(
            @Parameter(description = "Class ID", required = true) @PathVariable Long classId) {

        try {
            List<Booking> waitlist = bookingService.getWaitlistForClass(classId);

            if (waitlist.isEmpty()) {
                return new ApiResponse<>(HttpStatus.OK, "No waitlisted users for this class.", null);
            }

            return new ApiResponse<>(HttpStatus.OK, "Waitlist retrieved successfully.", waitlist);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving the waitlist.", null);
        }
    }

    @Operation(summary = "Check-in to a class", description = "Allows a user to check into a class.")
    @PostMapping("/checkin")
    public ResponseEntity<String> checkInToClass(
            @Parameter(description = "User ID", required = true) @RequestParam Long userId,
            @Parameter(description = "Class ID", required = true) @RequestParam Long classId) {

        boolean success = bookingService.checkInToClass(userId, classId);

        if (success) {
            return ResponseEntity.ok("Checked in successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Class has not started or class not found.");
        }
    }
}
