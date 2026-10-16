package com.crossguard.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="security_incidents", indexes=@Index(name="idx_incident_user_time", columnList="user_id,created_at"))
public class SecurityIncident {
    @Id private UUID id;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="event_id", nullable=false) private UUID eventId;
    @Column(nullable=false, length=180) private String title;
    @Column(nullable=false, length=16) private String severity;
    @Column(nullable=false, length=24) private String status;
    @Column(nullable=false, length=700) private String summary;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected SecurityIncident() {}
    public SecurityIncident(UUID userId, UUID eventId, String title, String severity, String summary) {
        this.id=UUID.randomUUID(); this.userId=userId; this.eventId=eventId; this.title=title; this.severity=severity; this.summary=summary; this.status="OPEN"; this.createdAt=Instant.now(); this.updatedAt=this.createdAt;
    }
    public void setStatus(String status){this.status=status;this.updatedAt=Instant.now();}
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getEventId(){return eventId;} public String getTitle(){return title;} public String getSeverity(){return severity;} public String getStatus(){return status;} public String getSummary(){return summary;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
