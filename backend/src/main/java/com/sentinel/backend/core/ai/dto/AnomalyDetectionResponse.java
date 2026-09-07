package com.sentinel.backend.core.ai.dto;

import com.sentinel.backend.core.ai.AnomalyDetection;

import java.time.Instant;
import java.util.UUID;

public record AnomalyDetectionResponse(
        UUID id,
        UUID eventId,
        String modelVersion,
        Double anomalyScore,
        Boolean anomaly,
        Double confidence,
        String reason,
        String status,
        Instant detectedAt
) {
    public static AnomalyDetectionResponse from(AnomalyDetection d) {
        return new AnomalyDetectionResponse(
                d.getId(), d.getEvent().getId(), d.getModelVersion(),
                d.getAnomalyScore(), d.getAnomaly(), d.getConfidence(),
                d.getReason(), d.getStatus(), d.getDetectedAt()
        );
    }
}
