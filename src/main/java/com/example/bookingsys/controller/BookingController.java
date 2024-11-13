package com.example.bookingsys.controller;

import com.example.bookingsys.model.Booking;
import com.example.bookingsys.service.BookingService;
import com.example.bookingsys.dto.ApiResponse;
import com.example.bookingsys.exception.ClassFullException;
import com.example.bookingsys.exception.InsufficientCreditsException;
import com.example.bookingsys.exception.BookingNotFoundException;
import com.example.bookingsys.exception.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping("/book")
    public ApiResponse<Booking> bookClass(@RequestParam Long userId, @RequestParam Long classId) {
        try {
            // Call the booking service to book the class
            Booking booking = bookingService.bookClass(userId, classId);

            // If the booking returns null, the class was full and the user has been added to the waitlist
            if (booking == null) {
                return new ApiResponse<>(HttpStatus.OK, "Class is full, added to the waitlist.", null);
            }

            // Return a success response with the booking details
            return new ApiResponse<>(HttpStatus.CREATED, "Class booked successfully.", booking);

        } catch (ClassFullException e) {
            // Handle the case where the class is full and the user is added to the waitlist
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Class is full. Added to the waitlist.", null);
        } catch (InsufficientCreditsException e) {
            // Handle the case where the user has insufficient credits to book the class
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Insufficient credits to book the class.", null);
        } catch (GenericException e) {
            // Handle any other generic exceptions
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while booking the class.", null);
        }
    }

    @PostMapping("/cancel/{bookingId}")
    public ApiResponse<String> cancelBooking(@PathVariable Long bookingId) {
        try {
            // Call the cancel method from the booking service
            bookingService.cancelBooking(bookingId);

            // Return a success response
            return new ApiResponse<>(HttpStatus.OK, "Booking canceled successfully.", "Booking ID: " + bookingId);

        } catch (BookingNotFoundException e) {
            // Handle the case where the booking is not found
            return new ApiResponse<>(HttpStatus.NOT_FOUND, "Booking not found.", null);
        } catch (Exception e) {
            // Catch any unexpected errors
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while canceling the booking.", null);
        }
    }

    @PostMapping("/end/{classId}")
    public ApiResponse<String> handleEndOfClass(@PathVariable Long classId) {
        try {
            // Call the service to handle end of class processing
            bookingService.handleEndOfClass(classId);

            // Return a success response
            return new ApiResponse<>(HttpStatus.OK, "Handled end of class and processed waitlisted users.", null);

        } catch (Exception e) {
            // Catch any unexpected errors
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the end of class.", null);
        }
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Booking>> getBookingsForUser(@PathVariable Long userId) {
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
    @GetMapping("/{bookingId}")
    public ApiResponse<Booking> getBookingById(@PathVariable Long bookingId) {
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

    @PutMapping("/{bookingId}")
    public ApiResponse<Booking> updateBooking(@PathVariable Long bookingId, @RequestBody Booking updatedBooking) {
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
    @PostMapping("/confirm/{bookingId}")
    public ApiResponse<String> confirmBooking(@PathVariable Long bookingId) {
        try {
            bookingService.confirmBooking(bookingId);
            return new ApiResponse<>(HttpStatus.OK, "Booking confirmed successfully.", "Booking ID: " + bookingId);
        } catch (BookingNotFoundException e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, "Booking not found.", null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while confirming the booking.", null);
        }
    }


    @GetMapping("/waitlist/{classId}")
    public ApiResponse<List<Booking>> getWaitlistForClass(@PathVariable Long classId) {
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




}
