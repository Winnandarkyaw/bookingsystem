package com.example.bookingsys.repository;

import com.example.bookingsys.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndActiveTrue(Long userId);
    List<Booking> findByClassIdAndActiveTrue(Long classId);
}
