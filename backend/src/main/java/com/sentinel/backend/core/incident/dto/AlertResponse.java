package com.sentinel.backend.core.incident.dto;

import com.sentinel.backend.core.incident.*;
import com.sentinel.backend.core.risk.RiskLevel;
import java.time.Instant;
import java.util.UUID;

public record AlertResponse(UUID id, UUID eventId, UUID incidentId, String title, String description,
                            int riskScore, RiskLevel riskLevel, AlertStatus status, Instant createdAt,
                            Instant acknowledgedAt, Instant resolvedAt) {
    public static AlertResponse from(Alert a){
        return new AlertResponse(a.getId(), a.getEvent().getId(), a.getIncident()==null?null:a.getIncident().getId(),
                a.getTitle(), a.getDescription(), a.getRiskScore(), a.getRiskLevel(), a.getStatus(),
                a.getCreatedAt(), a.getAcknowledgedAt(), a.getResolvedAt());
    }
}
