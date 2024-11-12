package com.example.bookingsys.repository;


import com.example.bookingsys.model.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    // This method finds all waitlisted users by classId
    List<Waitlist> findByClassId(Long classId);

    // Find all waitlisted users for a particular class ordered by the time they were added
    List<Waitlist> findByClassIdOrderByAddedTimeAsc(Long classId);

    // Remove a user from the waitlist (by user ID and class ID)
    void deleteByUserIdAndClassId(Long userId, Long classId);

    // Check if a user is already on the waitlist for a class
    boolean existsByUserIdAndClassId(Long userId, Long classId);
}
