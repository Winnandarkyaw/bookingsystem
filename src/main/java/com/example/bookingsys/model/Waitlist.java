package com.example.bookingsys.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@RedisHash("waitlist")
public class Waitlist {

    private Long id;
    private Long userId;
    private Long classId;
    private LocalDateTime addedTime;

    // Constructor to initialize fields
    public Waitlist(Long userId, Long classId, LocalDateTime addedTime) {
        this.userId = userId;
        this.classId = classId;
        this.addedTime = addedTime;
    }
}

