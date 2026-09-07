package com.sentinel.backend.core.ai;

import com.sentinel.backend.core.event.Event;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "anomaly_detections", indexes = {
        @Index(name = "idx_anomaly_detected_at", columnList = "detected_at"),
        @Index(name = "idx_anomaly_event_id", columnList = "event_id"),
        @Index(name = "idx_anomaly_is_anomaly", columnList = "is_anomaly")
})
public class AnomalyDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(name = "model_version", nullable = false, length = 80)
    private String modelVersion;

    @Column(name = "anomaly_score")
    private Double anomalyScore;

    @Column(name = "is_anomaly")
    private Boolean anomaly;

    @Column
    private Double confidence;

    @Column(nullable = false, length = 2000)
    private String reason;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private Instant detectedAt;

    @PrePersist
    void onCreate() { detectedAt = Instant.now(); }

    public UUID getId() { return id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public Double getAnomalyScore() { return anomalyScore; }
    public void setAnomalyScore(Double anomalyScore) { this.anomalyScore = anomalyScore; }
    public Boolean getAnomaly() { return anomaly; }
    public void setAnomaly(Boolean anomaly) { this.anomaly = anomaly; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getDetectedAt() { return detectedAt; }
}
