package com.crossguard.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="user_accounts", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class UserAccount {
    @Id private UUID id;
    @Column(nullable=false, length=254) private String email;
    @Column(nullable=false, length=80) private String displayName;
    @Column(nullable=false, length=100) private String passwordHash;
    @Column(nullable=false) private Instant createdAt;
    protected UserAccount() {}
    public UserAccount(String email, String displayName, String passwordHash) {
        this.id=UUID.randomUUID(); this.email=email; this.displayName=displayName; this.passwordHash=passwordHash; this.createdAt=Instant.now();
    }
    public UUID getId(){return id;} public String getEmail(){return email;} public String getDisplayName(){return displayName;} public String getPasswordHash(){return passwordHash;} public Instant getCreatedAt(){return createdAt;}
}
