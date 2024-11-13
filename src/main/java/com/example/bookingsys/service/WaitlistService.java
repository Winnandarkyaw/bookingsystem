package com.example.bookingsys.service;

import com.example.bookingsys.model.Waitlist;
import com.example.bookingsys.repository.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    public Optional<Waitlist> promoteFromWaitlist(Long classId) {
        List<Waitlist> waitlist = waitlistRepository.findByClassIdOrderByAddedTimeAsc(classId);
        if (!waitlist.isEmpty()) {
            Waitlist nextUser = waitlist.get(0);
            waitlistRepository.delete(nextUser);
            notifyWaitlistUpdate(nextUser, true);
            return Optional.of(nextUser);
        }
        return Optional.empty();
    }

    public int getWaitlistPosition(Long userId, Long classId) {
        List<Waitlist> waitlist = waitlistRepository.findByClassIdOrderByAddedTimeAsc(classId);
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return -1;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void removeExpiredWaitlistEntries() {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(2);
        List<Waitlist> expiredEntries = waitlistRepository.findByAddedTimeBefore(expirationTime);
        for (Waitlist entry : expiredEntries) {
            waitlistRepository.delete(entry);
            notifyUser(entry.getUserId(), "Your waitlist entry has expired.");
        }
    }

    public boolean addToWaitlist(Long userId, Long classId, int maxWaitlistSize) {
        if (isWaitlistFull(classId, maxWaitlistSize)) {
            notifyUser(userId, "The waitlist is full.");
            return false;
        }
        Waitlist waitlistEntry = new Waitlist(userId, classId, LocalDateTime.now());
        waitlistRepository.save(waitlistEntry);
        notifyWaitlistUpdate(waitlistEntry, false);
        return true;
    }

    public boolean isWaitlistFull(Long classId, int maxWaitlistSize) {
        return waitlistRepository.countByClassId(classId) >= maxWaitlistSize;
    }

    public void notifyUser(Long userId, String message) {
        System.out.println("Notification sent to user " + userId + ": " + message);
    }

    public void notifyWaitlistUpdate(Waitlist waitlist, boolean promoted) {
        String message = promoted ? "Promoted from waitlist!" : "Added to waitlist.";
        notifyUser(waitlist.getUserId(), message);
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendWaitlistReminders() {
        LocalDateTime reminderTime = LocalDateTime.now().minusDays(1);
        List<Waitlist> reminderEntries = waitlistRepository.findByAddedTimeBefore(reminderTime);
        for (Waitlist entry : reminderEntries) {
            notifyUser(entry.getUserId(), "Reminder: waitlist for class " + entry.getClassId());
        }
    }

    public void addToWaitlist(Long classId, Long userId) {
        LocalDateTime addedTime = LocalDateTime.now();
        Waitlist waitlistEntry = new Waitlist(userId, classId, addedTime);
        waitlistRepository.save(waitlistEntry);
    }
}
