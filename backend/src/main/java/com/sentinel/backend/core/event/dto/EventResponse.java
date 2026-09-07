package com.sentinel.backend.core.event.dto;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.event.EventSeverity;
import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id, UUID eventSourceId, String eventSourceName, String eventType,
        EventSeverity severity, Instant timestamp, String sourceIp, String username,
        String message, String metadata, Instant ingestedAt
) {
    public static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getEventSource().getId(), e.getEventSource().getName(),
                e.getEventType(), e.getSeverity(), e.getEventTimestamp(), e.getSourceIp(),
                e.getUsername(), e.getMessage(), e.getMetadataJson(), e.getIngestedAt());
    }
}
