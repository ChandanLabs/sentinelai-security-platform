package com.crossguard.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="security_events", indexes={@Index(name="idx_event_user_time", columnList="user_id,occurred_at"), @Index(name="idx_event_demo_time", columnList="demo_session_id,occurred_at")})
public class SecurityEvent {
    @Id private UUID id;
    @Column(name="user_id") private UUID userId;
    @Column(name="demo_session_id", length=36) private String demoSessionId;
    @Column(nullable=false, length=120) private String actor;
    @Column(nullable=false, length=80) private String action;
    @Column(nullable=false, length=180) private String resource;
    @Column(nullable=false, length=16) private String outcome;
    @Column(nullable=false, length=16) private String severity;
    @Column(nullable=false, length=700) private String explanation;
    @Column(name="occurred_at", nullable=false) private Instant occurredAt;
    protected SecurityEvent() {}
    public SecurityEvent(UUID id, UUID userId, String demoSessionId, String actor, String action, String resource, String outcome, String severity, String explanation, Instant occurredAt) {
        this.id=id; this.userId=userId; this.demoSessionId=demoSessionId; this.actor=actor; this.action=action; this.resource=resource; this.outcome=outcome; this.severity=severity; this.explanation=explanation; this.occurredAt=occurredAt;
    }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public String getDemoSessionId(){return demoSessionId;} public String getActor(){return actor;} public String getAction(){return action;} public String getResource(){return resource;} public String getOutcome(){return outcome;} public String getSeverity(){return severity;} public String getExplanation(){return explanation;} public Instant getOccurredAt(){return occurredAt;}
}
