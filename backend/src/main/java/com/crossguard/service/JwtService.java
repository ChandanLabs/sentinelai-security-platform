package com.crossguard.service;

import com.crossguard.model.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key; private final long hours;
    public JwtService(@Value("${crossguard.jwt-secret}") String secret, @Value("${crossguard.jwt-hours}") long hours) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes.");
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.hours=hours;
    }
    public String issue(UserAccount user) {
        Instant now=Instant.now();
        return Jwts.builder().subject(user.getId().toString()).claim("email",user.getEmail()).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(hours*3600))).signWith(key).compact();
    }
    public UUID userId(String token) {
        Claims claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }
}
