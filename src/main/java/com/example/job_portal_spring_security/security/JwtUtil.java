package com.example.job_portal_spring_security.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key key;
    private final long accessTtlMillis;
    private final long refreshTtlMillis;
    private final String issuer;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.access-ttl-minutes}") long accessTtlMinutes,
                   @Value("${app.jwt.refresh-ttl-days}") long refreshTtlDays,
                   @Value("${app.jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMillis = accessTtlMinutes * 60_000L;
        this.refreshTtlMillis = refreshTtlDays * 24L * 60L * 60L * 1000L;
        this.issuer = issuer;
    }

    private String generate(String subject, long ttlMillis, String type) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMillis);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .setIssuer(issuer)
                .addClaims(Map.of("typ", type))    // typ=access|refresh
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String subject) {
        return generate(subject, accessTtlMillis, "access");
    }

    public String generateRefreshToken(String subject) {
        return generate(subject, refreshTtlMillis, "refresh");
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public java.security.Key getSigningKey() {
        return key;
    }

}
