package com.sentinel.backend.auth;

import com.sentinel.backend.auth.dto.*;
import com.sentinel.backend.user.*;
import com.sentinel.backend.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final AuditEventRepository audits;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuthenticationManager authenticationManager;
    private final long refreshExpirationMs;

    public AuthService(
            UserRepository users,
            RefreshTokenRepository refreshTokens,
            AuditEventRepository audits,
            PasswordEncoder encoder,
            JwtService jwt,
            AuthenticationManager authenticationManager,
            @org.springframework.beans.factory.annotation.Value("${security.jwt.refresh-expiration-ms:604800000}")
            long refreshExpirationMs) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.audits = audits;
        this.encoder = encoder;
        this.jwt = jwt;
        this.authenticationManager = authenticationManager;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public UserResponse register(RegisterRequest req) {
        String email = normalize(req.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.VIEWER);
        u.setEnabled(true);
        users.save(u);
        audit(u, "REGISTER", "SUCCESS", email);
        return UserResponse.from(u);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        String email = normalize(req.email());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.password()));
        } catch (AuthenticationException e) {
            audit(null, "LOGIN", "FAILURE", email);
            throw new BadCredentialsException("Invalid email or password");
        }
        User u = users.findByEmailIgnoreCase(email).orElseThrow();
        AuthResponse response = issue(u);
        audit(u, "LOGIN", "SUCCESS", email);
        return response;
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        String hash = hash(req.refreshToken());
        RefreshToken old = refreshTokens.findByTokenHash(hash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));
        if (!old.isActive()) {
            throw new SecurityException("Refresh token is expired or revoked");
        }
        if (!old.getUser().isEnabled()) {
            throw new SecurityException("User account is disabled");
        }
        old.revoke();
        refreshTokens.save(old);
        AuthResponse response = issue(old.getUser());
        audit(old.getUser(), "TOKEN_REFRESH", "SUCCESS", old.getUser().getEmail());
        return response;
    }

    @Transactional
    public void logout(LogoutRequest req) {
        refreshTokens.findByTokenHash(hash(req.refreshToken())).ifPresent(t -> {
            if (t.isActive()) {
                t.revoke();
                refreshTokens.save(t);
                audit(t.getUser(), "LOGOUT", "SUCCESS", t.getUser().getEmail());
            }
        });
    }

    public UserResponse me(String email) {
        return UserResponse.from(
                users.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new SecurityException("User not found")));
    }

    private AuthResponse issue(User u) {
        String raw = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(randomBytes(32));
        RefreshToken rt = new RefreshToken();
        rt.setUser(u);
        rt.setTokenHash(hash(raw));
        rt.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        refreshTokens.save(rt);
        return new AuthResponse(
                jwt.generateAccessToken(u),
                raw,
                jwt.getExpirationMs() / 1000,
                UserResponse.from(u));
    }

    private void audit(User u, String action, String outcome, String email) {
        AuditEvent a = new AuditEvent();
        a.setUser(u);
        a.setAction(action);
        a.setOutcome(outcome);
        a.setEmail(email);
        audits.save(a);
    }

    private static byte[] randomBytes(int n) {
        byte[] b = new byte[n];
        new SecureRandom().nextBytes(b);
        return b;
    }

    private static String hash(String value) {
        try {
            byte[] b = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder s = new StringBuilder();
            for (byte x : b) {
                s.append(String.format("%02x", x));
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
