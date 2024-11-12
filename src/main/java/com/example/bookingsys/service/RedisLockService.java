package com.example.bookingsys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean acquireLock(String key, long timeout, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "locked", timeout, timeUnit));
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
