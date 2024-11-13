package com.example.bookingsys.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor  // Lombok will generate a no-argument constructor
@AllArgsConstructor // Lombok will generate a constructor with all arguments
@RedisHash("AvailableClass")
public class AvailableClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String countryCode;
    private int requiredCredits;
    private int totalSlots;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(name = "current_booked_slots")
    private int currentBookedSlots;
}
