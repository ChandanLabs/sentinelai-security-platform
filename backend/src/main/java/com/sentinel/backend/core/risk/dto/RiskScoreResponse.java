package com.sentinel.backend.core.risk.dto;

import com.sentinel.backend.core.risk.*;
import java.time.Instant;
import java.util.UUID;

public record RiskScoreResponse(UUID id, UUID eventId, int score, RiskLevel level, double ruleComponent,
                                double aiComponent, int ruleCount, boolean aiAvailable, String explanation,
                                Instant calculatedAt) {
    public static RiskScoreResponse from(RiskScore r) {
        return new RiskScoreResponse(r.getId(), r.getEvent().getId(), r.getScore(), r.getLevel(),
                r.getRuleComponent(), r.getAiComponent(), r.getRuleCount(), r.getAiAvailable(),
                r.getExplanation(), r.getCalculatedAt());
    }
}
