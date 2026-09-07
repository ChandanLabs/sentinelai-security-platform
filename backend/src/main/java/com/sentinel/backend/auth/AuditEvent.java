package com.sentinel.backend.auth;

import com.sentinel.backend.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="audit_events", indexes={
    @Index(name="idx_audit_event_created_at", columnList="created_at"),
    @Index(name="idx_audit_event_action", columnList="action")
})
public class AuditEvent {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;
    @Column(nullable=false,length=50) private String action;
    @Column(nullable=false,length=20) private String outcome;
    @Column(length=255) private String email;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @PrePersist void onCreate(){createdAt=Instant.now();}
    public void setUser(User user){this.user=user;} public void setAction(String action){this.action=action;} public void setOutcome(String outcome){this.outcome=outcome;} public void setEmail(String email){this.email=email;}
}
