package com.example.demo.Auth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BlacklistCleanupTask {

    private final BlacklistedTokenRepository blacklistRepo;

    public BlacklistCleanupTask(BlacklistedTokenRepository blacklistRepo) {
        this.blacklistRepo = blacklistRepo;
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        blacklistRepo.deleteByExpiryBefore(new java.util.Date());
    }
}