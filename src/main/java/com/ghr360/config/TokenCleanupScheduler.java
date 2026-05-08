package com.ghr360.config;

import com.ghr360.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Periodically removes expired tokens from the blacklist table
 * so the table does not grow unbounded.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    // Runs every hour
    @Scheduled(fixedRate = 3_600_000)
    public void cleanExpiredTokens() {
        blacklistedTokenRepository.deleteExpiredTokens(Instant.now());
        log.debug("Expired blacklisted tokens cleaned up.");
    }
}
