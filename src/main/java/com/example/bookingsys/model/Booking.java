package com.example.bookingsys.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor  // Lombok will generate a no-argument constructor
@AllArgsConstructor
@RedisHash("booking")

public class Booking {

    private Long id;
    private Long userId;
    private Long classId;
    private LocalDateTime bookingTime;
    private boolean isActive;
    private int creditsUsed;
    private boolean isConfirmed;
    private LocalDateTime classStartTime;
    private LocalDateTime classEndTime;
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

