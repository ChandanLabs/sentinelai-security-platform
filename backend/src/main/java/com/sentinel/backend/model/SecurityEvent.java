package com.sentinel.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_events", indexes = {
        @Index(name = "idx_events_type_source_time", columnList = "event_type, source_ip, event_timestamp")
})
public class SecurityEvent {
    @Id
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false, length = 16)
    private String severity;

    @Column(name = "event_timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "source_ip", nullable = false, length = 64)
    private String sourceIp;

    @Column(nullable = false, length = 128)
    private String username;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @Column(name = "detection_rule", nullable = false, length = 64)
    private String detectionRule;

    @Column(name = "ai_analysis_status", nullable = false, length = 32)
    private String aiAnalysisStatus;

    protected SecurityEvent() {
    }

    public SecurityEvent(
            UUID id, String eventType, String severity, Instant timestamp,
            String sourceIp, String username, String message, int riskScore,
            String detectionRule, String aiAnalysisStatus) {
        this.id = id;
        this.eventType = eventType;
        this.severity = severity;
        this.timestamp = timestamp;
        this.sourceIp = sourceIp;
        this.username = username;
        this.message = message;
        this.riskScore = riskScore;
        this.detectionRule = detectionRule;
        this.aiAnalysisStatus = aiAnalysisStatus;
    }

    public UUID getId() { return id; }
    public String getEventType() { return eventType; }
    public String getSeverity() { return severity; }
    public Instant getTimestamp() { return timestamp; }
    public String getSourceIp() { return sourceIp; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
    public int getRiskScore() { return riskScore; }
    public String getDetectionRule() { return detectionRule; }
    public String getAiAnalysisStatus() { return aiAnalysisStatus; }
}
