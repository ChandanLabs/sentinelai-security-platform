package com.sentinel.backend.core.detection;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.event.EventSeverity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rule_detections", indexes = {
        @Index(name = "idx_rule_detections_detected_at", columnList = "detected_at"),
        @Index(name = "idx_rule_detections_rule_code", columnList = "rule_code"),
        @Index(name = "idx_rule_detections_event_id", columnList = "event_id")
}, uniqueConstraints = @UniqueConstraint(name = "uk_rule_detection_event_rule", columnNames = {"event_id", "rule_code"}))
public class RuleDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "rule_code", nullable = false, length = 80)
    private String ruleCode;

    @Column(name = "rule_name", nullable = false, length = 160)
    private String ruleName;

    @Column(nullable = false, length = 2000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventSeverity severity;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private Instant detectedAt;

    @PrePersist
    void onCreate() {
        detectedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public EventSeverity getSeverity() { return severity; }
    public void setSeverity(EventSeverity severity) { this.severity = severity; }
    public Instant getDetectedAt() { return detectedAt; }
}
