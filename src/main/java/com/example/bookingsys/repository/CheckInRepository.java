package com.example.bookingsys.repository;

import com.example.bookingsys.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    // You can add custom queries here if needed, such as find by userId or classId
}
