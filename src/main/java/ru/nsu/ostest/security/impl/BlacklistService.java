package ru.nsu.ostest.security.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlacklistService {

    private final Map<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public static final long EXPIRATION_TIME_IN_MILLIS = 15 * 60 * 1000;

    public void addToBlacklist(String token) {
        tokenBlacklist.put(token, System.currentTimeMillis());
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();

        tokenBlacklist.entrySet().removeIf(entry ->
                (currentTime - entry.getValue()) > EXPIRATION_TIME_IN_MILLIS
        );
    }

}
