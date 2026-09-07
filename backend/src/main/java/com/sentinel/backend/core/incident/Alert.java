package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.risk.RiskLevel;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_alerts_created_at", columnList = "created_at"),
        @Index(name = "idx_alerts_status", columnList = "status"),
        @Index(name = "idx_alerts_risk_level", columnList = "risk_level"),
        @Index(name = "idx_alerts_incident_id", columnList = "incident_id")
}, uniqueConstraints = @UniqueConstraint(name = "uk_alert_event", columnNames = "event_id"))
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public UUID getId(){return id;}
    public Event getEvent(){return event;} public void setEvent(Event v){event=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public Integer getRiskScore(){return riskScore;} public void setRiskScore(Integer v){riskScore=v;}
    public RiskLevel getRiskLevel(){return riskLevel;} public void setRiskLevel(RiskLevel v){riskLevel=v;}
    public AlertStatus getStatus(){return status;} public void setStatus(AlertStatus v){status=v;}
    public Incident getIncident(){return incident;} public void setIncident(Incident v){incident=v;}
    public Instant getCreatedAt(){return createdAt;}
    public Instant getAcknowledgedAt(){return acknowledgedAt;} public void setAcknowledgedAt(Instant v){acknowledgedAt=v;}
    public Instant getResolvedAt(){return resolvedAt;} public void setResolvedAt(Instant v){resolvedAt=v;}
}
