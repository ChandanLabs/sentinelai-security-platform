package com.sentinel.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_alerts", indexes = {
        @Index(name = "idx_alerts_created_at", columnList = "created_at")
})
public class AlertRecord {
    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 16)
    private String severity;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "source_ip", nullable = false, length = 64)
    private String sourceIp;

    @Column(name = "event_count", nullable = false)
    private int eventCount;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "detection_rule", nullable = false, length = 64)
    private String detectionRule;

    @Column(name = "owner_id")
    private UUID ownerId;

    protected AlertRecord() {
    }

    public AlertRecord(UUID id, String title, String severity, String status,
                       Instant createdAt, String sourceIp, int eventCount,
                       UUID eventId, String detectionRule) {
        this.id = id;
        this.title = title;
        this.severity = severity;
        this.status = status;
        this.createdAt = createdAt;
        this.sourceIp = sourceIp;
        this.eventCount = eventCount;
        this.eventId = eventId;
        this.detectionRule = detectionRule;
    }

    public AlertRecord(UUID id, String title, String severity, String status,
                       Instant createdAt, String sourceIp, int eventCount,
                       UUID eventId, String detectionRule, UUID ownerId) {
        this(id, title, severity, status, createdAt, sourceIp, eventCount, eventId, detectionRule);
        this.ownerId = ownerId;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getSeverity() { return severity; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public String getSourceIp() { return sourceIp; }
    public int getEventCount() { return eventCount; }
    public UUID getEventId() { return eventId; }
    public String getDetectionRule() { return detectionRule; }
    public UUID getOwnerId() { return ownerId; }
    public void setEventCount(int eventCount) { this.eventCount = eventCount; }
}
