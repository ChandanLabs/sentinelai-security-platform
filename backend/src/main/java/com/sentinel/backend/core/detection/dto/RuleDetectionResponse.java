package com.sentinel.backend.core.detection.dto;

import com.sentinel.backend.core.detection.RuleDetection;
import com.sentinel.backend.core.event.EventSeverity;

import java.time.Instant;
import java.util.UUID;

public record RuleDetectionResponse(
        UUID id,
        UUID eventId,
        String ruleCode,
        String ruleName,
        EventSeverity severity,
        String reason,
        Instant detectedAt
) {
    public static RuleDetectionResponse from(RuleDetection detection) {
        return new RuleDetectionResponse(
                detection.getId(),
                detection.getEvent().getId(),
                detection.getRuleCode(),
                detection.getRuleName(),
                detection.getSeverity(),
                detection.getReason(),
                detection.getDetectedAt()
        );
    }
}
