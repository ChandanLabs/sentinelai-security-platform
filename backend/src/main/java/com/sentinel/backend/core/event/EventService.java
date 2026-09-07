package com.sentinel.backend.core.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.backend.core.event.dto.*;
import com.sentinel.backend.core.eventsource.EventSource;
import com.sentinel.backend.core.eventsource.EventSourceRepository;
import com.sentinel.backend.core.detection.RuleDetectionService;
import com.sentinel.backend.core.ai.AnomalyDetectionService;
import com.sentinel.backend.core.risk.RiskScoreService;
import com.sentinel.backend.core.incident.AlertService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class EventService {
    private final EventRepository events;
    private final EventSourceRepository sources;
    private final ObjectMapper objectMapper;
    private final RuleDetectionService ruleDetectionService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final RiskScoreService riskScoreService;
    private final AlertService alertService;

    public EventService(EventRepository events, EventSourceRepository sources, ObjectMapper objectMapper, RuleDetectionService ruleDetectionService, AnomalyDetectionService anomalyDetectionService, RiskScoreService riskScoreService, AlertService alertService) {
        this.events = events; this.sources = sources; this.objectMapper = objectMapper; this.ruleDetectionService = ruleDetectionService; this.anomalyDetectionService = anomalyDetectionService; this.riskScoreService = riskScoreService; this.alertService = alertService;
    }

    public EventResponse ingest(EventRequest request) {
        EventSource source = sources.findById(request.eventSourceId())
                .orElseThrow(() -> new NoSuchElementException("Event source not found"));
        if (!source.isActive()) throw new IllegalStateException("Event source is inactive");

        Event event = new Event();
        event.setEventSource(source);
        event.setEventType(normalizeType(request.eventType()));
        event.setSeverity(parseSeverity(request.severity()));
        event.setEventTimestamp(request.timestamp() == null ? java.time.Instant.now() : request.timestamp());
        event.setSourceIp(trimToNull(request.sourceIp()));
        event.setUsername(trimToNull(request.username()));
        event.setMessage(request.message().trim());
        event.setMetadataJson(normalizeMetadata(request.metadata()));
        Event saved = events.save(event);
        ruleDetectionService.evaluate(saved);
        anomalyDetectionService.analyze(saved);
        riskScoreService.calculate(saved);
        alertService.generateIfRequired(saved);
        return EventResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> all(int page, int size) {
        return events.findAllByOrderByEventTimestampDesc(PageRequest.of(page, Math.min(size, 100)))
                .map(EventResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> bySource(UUID sourceId, int page, int size) {
        if (!sources.existsById(sourceId)) throw new NoSuchElementException("Event source not found");
        return events.findByEventSourceIdOrderByEventTimestampDesc(sourceId, PageRequest.of(page, Math.min(size, 100)))
                .map(EventResponse::from);
    }

    @Transactional(readOnly = true)
    public EventResponse one(UUID id) {
        return EventResponse.from(events.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found")));
    }

    private String normalizeType(String value) { return value.trim().toLowerCase(Locale.ROOT); }
    private EventSeverity parseSeverity(String value) { return EventSeverity.valueOf(value.trim().toUpperCase(Locale.ROOT)); }
    private String trimToNull(String value) { if (value == null) return null; String v = value.trim(); return v.isEmpty() ? null : v; }
    private String normalizeMetadata(String value) {
        if (value == null || value.isBlank()) return null;
        try { return objectMapper.readTree(value).toString(); }
        catch (Exception ex) { throw new IllegalArgumentException("metadata must be valid JSON"); }
    }
}
