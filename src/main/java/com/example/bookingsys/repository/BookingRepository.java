package com.example.bookingsys.repository;

import com.example.bookingsys.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndActiveTrue(Long userId);
    List<Booking> findByClassIdAndActiveTrue(Long classId);
}
