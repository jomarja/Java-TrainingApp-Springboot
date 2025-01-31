package com.trainignapp.trainingapp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceProtectionService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long BLOCK_TIME_MINUTES = 5;
    private final Map<String, FailedLoginAttempt> failedAttempts = new ConcurrentHashMap<>();

    public void recordFailedLogin(String username) {
        failedAttempts.compute(username, (key, attempt) -> {
            if (attempt == null) {
                return new FailedLoginAttempt(1, null);
            }
            int newCount = attempt.failedAttempts + 1;
            LocalDateTime blockTime = (newCount >= MAX_FAILED_ATTEMPTS) ? LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES) : null;
            return new FailedLoginAttempt(newCount, blockTime);
        });
    }

    public boolean isBlocked(String username) {
        FailedLoginAttempt attempt = failedAttempts.get(username);
        if (attempt == null || attempt.blockTime == null) {
            return false;
        }
        if (attempt.blockTime.isBefore(LocalDateTime.now())) {
            failedAttempts.remove(username);
            return false;
        }
        return true;
    }

    public void resetFailedAttempts(String username) {
        failedAttempts.remove(username);
    }

    private static class FailedLoginAttempt {
        private final int failedAttempts;
        private final LocalDateTime blockTime;

        public FailedLoginAttempt(int failedAttempts, LocalDateTime blockTime) {
            this.failedAttempts = failedAttempts;
            this.blockTime = blockTime;
        }
    }
}
