package com.example.bookingsys.repository;

import com.example.bookingsys.model.AvailableClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableClassRepository extends JpaRepository<AvailableClass, Long> {

    // Find a class by its ID (already provided by JpaRepository)
    Optional<AvailableClass> findById(Long classId);

    // You can use this method to check if a class has available slots
    boolean existsByIdAndCurrentBookedSlotsLessThan(Long classId, int currentBookedSlots);
}
