package com.sentinel.backend.dto;

import com.sentinel.backend.model.IncidentRecord;

import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String title,
        String severity,
        String status,
        Instant createdAt,
        Instant updatedAt,
        int alertCount,
        UUID alertId) {
    public static IncidentResponse from(IncidentRecord incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                1,
                incident.getAlertId());
    }
}
