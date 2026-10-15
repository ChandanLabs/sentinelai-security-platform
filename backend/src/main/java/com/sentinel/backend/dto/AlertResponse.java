package com.sentinel.backend.dto;

import com.sentinel.backend.model.AlertRecord;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        String title,
        String severity,
        String status,
        Instant createdAt,
        String sourceIp,
        int eventCount,
        UUID eventId,
        String detectionRule) {
    public static AlertResponse from(AlertRecord alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getTitle(),
                alert.getSeverity(),
                alert.getStatus(),
                alert.getCreatedAt(),
                alert.getSourceIp(),
                alert.getEventCount(),
                alert.getEventId(),
                alert.getDetectionRule());
    }
}
