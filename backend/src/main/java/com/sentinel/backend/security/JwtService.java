package com.sentinel.backend.security;

import com.sentinel.backend.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${security.jwt.secret}") String secret,@Value("${security.jwt.expiration-ms:900000}") long expirationMs){
        if(secret.length()<32) throw new IllegalArgumentException("security.jwt.secret must be at least 32 characters");
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expirationMs=expirationMs;
    }
    public String generateAccessToken(User user){
        Instant now=Instant.now();
        return Jwts.builder().subject(user.getEmail()).claim("role",user.getRole().name())
            .issuedAt(Date.from(now)).expiration(new Date(now.toEpochMilli()+expirationMs)).signWith(key).compact();
    }
    public String extractSubject(String token){return parse(token).getPayload().getSubject();}
    public boolean isValid(String token,User user){
        try { return user.isEnabled() && extractSubject(token).equalsIgnoreCase(user.getEmail()) && !parse(token).getPayload().getExpiration().before(new Date()); }
        catch(JwtException|IllegalArgumentException e){return false;}
    }
    public long getExpirationMs(){return expirationMs;}
    private Jws<Claims> parse(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);}
}
