package com.example.job_portal_spring_security.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory blacklist of JWTs until their expiration. Replace with Redis/DB in prod. */
@Component
public class TokenBlacklist {

    // token -> expiresAtEpochMillis
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, long expiresAtMillis) {
        blacklist.put(token, expiresAtMillis);
    }

    public boolean isBlacklisted(String token) {
        Long exp = blacklist.get(token);
        if (exp == null) return false;
        if (exp < Instant.now().toEpochMilli()) { // auto-clean expired
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}

