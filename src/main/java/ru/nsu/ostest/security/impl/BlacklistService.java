package ru.nsu.ostest.security.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlacklistService {

    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        tokenBlacklist.put(token, System.currentTimeMillis());
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredTokens() {
        long expirationTimeInMillis = AuthConstants.EXPIRATION_TIME_IN_MILLIS;
        long currentTime = System.currentTimeMillis();

        tokenBlacklist.entrySet().removeIf(entry ->
                (currentTime - entry.getValue()) > expirationTimeInMillis
        );
    }

}
