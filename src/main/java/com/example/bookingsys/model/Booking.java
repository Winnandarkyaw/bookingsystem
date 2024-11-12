package com.example.bookingsys.model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Booking {

    private Long id;
    private Long userId;
    private Long classId;
    private LocalDateTime bookingTime;
    private boolean isActive;
    private int creditsUsed;
    private boolean isConfirmed;

    // Constructor with 6 arguments
    public Booking(Long userId, Long classId, LocalDateTime bookingTime, boolean isActive, int creditsUsed, boolean isConfirmed) {
        this.userId = userId;
        this.classId = classId;
        this.bookingTime = bookingTime;
        this.isActive = isActive;
        this.creditsUsed = creditsUsed;
        this.isConfirmed = isConfirmed;
    }
}

