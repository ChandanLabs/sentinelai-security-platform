package com.sentinel.backend.core.risk;

import com.sentinel.backend.core.event.Event;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_scores", indexes = {
        @Index(name = "idx_risk_scores_event_id", columnList = "event_id"),
        @Index(name = "idx_risk_scores_score", columnList = "risk_score"),
        @Index(name = "idx_risk_scores_level", columnList = "risk_level"),
        @Index(name = "idx_risk_scores_calculated_at", columnList = "calculated_at")
})
public class RiskScore {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(name = "risk_score", nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel level;

    @Column(name = "rule_component", nullable = false)
    private Double ruleComponent;

    @Column(name = "ai_component", nullable = false)
    private Double aiComponent;

    @Column(name = "rule_count", nullable = false)
    private Integer ruleCount;

    @Column(name = "ai_available", nullable = false)
    private Boolean aiAvailable;

    @Column(nullable = false, length = 2000)
    private String explanation;

    @Column(name = "calculated_at", nullable = false, updatable = false)
    private Instant calculatedAt;

    @PrePersist void onCreate() { calculatedAt = Instant.now(); }

    public UUID getId(){return id;}
    public Event getEvent(){return event;} public void setEvent(Event v){event=v;}
    public Integer getScore(){return score;} public void setScore(Integer v){score=v;}
    public RiskLevel getLevel(){return level;} public void setLevel(RiskLevel v){level=v;}
    public Double getRuleComponent(){return ruleComponent;} public void setRuleComponent(Double v){ruleComponent=v;}
    public Double getAiComponent(){return aiComponent;} public void setAiComponent(Double v){aiComponent=v;}
    public Integer getRuleCount(){return ruleCount;} public void setRuleCount(Integer v){ruleCount=v;}
    public Boolean getAiAvailable(){return aiAvailable;} public void setAiAvailable(Boolean v){aiAvailable=v;}
    public String getExplanation(){return explanation;} public void setExplanation(String v){explanation=v;}
    public Instant getCalculatedAt(){return calculatedAt;}
}
