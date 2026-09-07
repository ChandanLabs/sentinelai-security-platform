package com.sentinel.backend.core.incident.dto;

import com.sentinel.backend.core.incident.*;
import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(UUID id, String title, String summary, IncidentSeverity severity,
                               IncidentStatus status, String createdBy, Instant createdAt,
                               Instant updatedAt, Instant resolvedAt) {
    public static IncidentResponse from(Incident i){
        return new IncidentResponse(i.getId(), i.getTitle(), i.getSummary(), i.getSeverity(), i.getStatus(),
                i.getCreatedBy(), i.getCreatedAt(), i.getUpdatedAt(), i.getResolvedAt());
    }
}
