package com.sentinel.backend.auth;

import com.sentinel.backend.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="refresh_tokens", indexes={
    @Index(name="idx_refresh_token_hash", columnList="token_hash", unique=true),
    @Index(name="idx_refresh_token_user", columnList="user_id")
})
public class RefreshToken {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="token_hash", nullable=false, unique=true, length=64)
    private String tokenHash;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @Column(name="revoked_at")
    private Instant revokedAt;

    @PrePersist void onCreate(){createdAt=Instant.now();}
    public UUID getId(){return id;}
    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}
    public String getTokenHash(){return tokenHash;}
    public void setTokenHash(String tokenHash){this.tokenHash=tokenHash;}
    public Instant getExpiresAt(){return expiresAt;}
    public void setExpiresAt(Instant expiresAt){this.expiresAt=expiresAt;}
    public Instant getCreatedAt(){return createdAt;}
    public Instant getRevokedAt(){return revokedAt;}
    public void revoke(){this.revokedAt=Instant.now();}
    public boolean isActive(){return revokedAt==null && expiresAt.isAfter(Instant.now());}
}
