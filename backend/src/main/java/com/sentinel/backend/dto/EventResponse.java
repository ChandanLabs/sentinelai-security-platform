package com.sentinel.backend.dto;

import com.sentinel.backend.model.SecurityEvent;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String eventType,
        String severity,
        Instant timestamp,
        String sourceIp,
        String username,
        String message,
        int riskScore,
        String detectionRule,
        String aiAnalysisStatus,
        UUID alertId,
        UUID incidentId,
        String actorId,
        String sessionId,
        String targetResource,
        String outcome,
        String detectionEvidence) {
    public static EventResponse from(SecurityEvent event) {
        return new EventResponse(
                event.getId(),
                event.getEventType(),
                event.getSeverity(),
                event.getTimestamp(),
                event.getSourceIp(),
                event.getUsername(),
                event.getMessage(),
                event.getRiskScore(),
                event.getDetectionRule(),
                event.getAiAnalysisStatus(),
                null,
                null,
                event.getUsername(),
                event.getSessionTag(),
                event.getTargetResource(),
                event.getOutcome(),
                event.getDetectionEvidence());
    }

    public static EventResponse from(SecurityEvent event, UUID alertId, UUID incidentId) {
        EventResponse base = from(event);
        return new EventResponse(
                base.id(), base.eventType(), base.severity(), base.timestamp(),
                base.sourceIp(), base.username(), base.message(), base.riskScore(),
                base.detectionRule(), base.aiAnalysisStatus(), alertId, incidentId,
                base.actorId(), base.sessionId(), base.targetResource(), base.outcome(),
                base.detectionEvidence());
    }
}
