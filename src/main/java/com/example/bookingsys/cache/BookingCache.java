package com.example.bookingsys.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class BookingCache {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String CLASS_AVAILABILITY_KEY = "class_availability:";

    // Check if a class is available for booking
    public boolean isClassAvailable(Long classId) {
        if (redisTemplate != null) {
            return Boolean.TRUE.equals(redisTemplate.hasKey(CLASS_AVAILABILITY_KEY + classId));
        }
        // Handle RedisTemplate being null, fallback behavior if needed
        return false;
    }

    // Set class as unavailable (with expiration)
    public void setClassUnavailable(Long classId) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(CLASS_AVAILABILITY_KEY + classId, "unavailable", 30, TimeUnit.MINUTES);
        }
    }

    // Set class as available
    public void setClassAvailable(Long classId) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(CLASS_AVAILABILITY_KEY + classId, "available");
        }
    }
}
