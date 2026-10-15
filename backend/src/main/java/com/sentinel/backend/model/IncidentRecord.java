package com.sentinel.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_incidents", indexes = {
        @Index(name = "idx_incidents_created_at", columnList = "created_at")
})
public class IncidentRecord {
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

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "alert_id", nullable = false)
    private UUID alertId;

    @Column(name = "owner_id")
    private UUID ownerId;

    protected IncidentRecord() {
    }

    public IncidentRecord(UUID id, String title, String severity, String status,
                          Instant createdAt, Instant updatedAt, UUID alertId) {
        this.id = id;
        this.title = title;
        this.severity = severity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.alertId = alertId;
    }

    public IncidentRecord(UUID id, String title, String severity, String status,
                          Instant createdAt, Instant updatedAt, UUID alertId, UUID ownerId) {
        this(id, title, severity, status, createdAt, updatedAt, alertId);
        this.ownerId = ownerId;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getSeverity() { return severity; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public UUID getAlertId() { return alertId; }
    public UUID getOwnerId() { return ownerId; }

    public void setStatus(String status) { this.status = status; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
