package com.sentinel.backend.service;

import com.sentinel.backend.dto.AlertResponse;
import com.sentinel.backend.dto.EventIngestRequest;
import com.sentinel.backend.dto.EventResponse;
import com.sentinel.backend.dto.IncidentResponse;
import com.sentinel.backend.model.AlertRecord;
import com.sentinel.backend.model.IncidentRecord;
import com.sentinel.backend.model.SecurityEvent;
import com.sentinel.backend.repository.AlertRepository;
import com.sentinel.backend.repository.IncidentRepository;
import com.sentinel.backend.repository.SecurityEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SecurityEventService {
    private final SecurityEventRepository eventRepository;
    private final AlertRepository alertRepository;
    private final IncidentRepository incidentRepository;

    public SecurityEventService(
            SecurityEventRepository eventRepository,
            AlertRepository alertRepository,
            IncidentRepository incidentRepository) {
        this.eventRepository = eventRepository;
        this.alertRepository = alertRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public synchronized EventResponse ingest(EventIngestRequest request) {
        Instant now = Instant.now();
        String rule = "";
        String severity = "INFO";
        int riskScore = 5;
        int matchedEventCount = 1;

        if ("login_failed".equals(request.eventType())) {
            long failures = eventRepository.countByEventTypeAndSourceIpAndTimestampAfter(
                    "login_failed", request.sourceIp(), now.minus(5, ChronoUnit.MINUTES)) + 1;
            matchedEventCount = Math.toIntExact(failures);
            severity = failures >= 5 ? "HIGH" : "MEDIUM";
            riskScore = failures >= 5 ? 85 : (int) Math.min(65, 25 + failures * 10);
            if (failures == 5) {
                rule = "AUTH_BRUTE_FORCE";
            }
        } else if ("privilege_escalation".equals(request.eventType())) {
            severity = "CRITICAL";
            riskScore = 95;
            rule = "PRIVILEGE_ESCALATION";
        } else if ("suspicious_command".equals(request.eventType())) {
            severity = "HIGH";
            riskScore = 78;
            rule = "SUSPICIOUS_COMMAND";
        }

        SecurityEvent event = eventRepository.save(new SecurityEvent(
                UUID.randomUUID(), request.eventType(), severity, now,
                request.sourceIp(), request.username(), request.message(),
                riskScore, rule, "NOT_CONFIGURED"));

        boolean bruteForceThreshold = "AUTH_BRUTE_FORCE".equals(rule);
        UUID alertId = null;
        UUID incidentId = null;
        if (bruteForceThreshold || "HIGH".equals(severity) || "CRITICAL".equals(severity)) {
            AlertIncidentLink link = createAlertAndIncident(
                    event,
                    rule.isBlank() ? request.eventType().toUpperCase() : rule,
                    bruteForceThreshold ? matchedEventCount : 1);
            alertId = link.alertId();
            incidentId = link.incidentId();
        }
        return EventResponse.from(event, alertId, incidentId);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> listEvents(Pageable pageable) {
        return eventRepository.findAllByOrderByTimestampDesc(pageable).map(EventResponse::from);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID id) {
        SecurityEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Event not found"));
        AlertRecord alert = alertRepository.findFirstByEventIdOrderByCreatedAtDesc(id).orElse(null);
        IncidentRecord incident = alert == null
                ? null
                : incidentRepository.findFirstByAlertId(alert.getId()).orElse(null);
        return EventResponse.from(
                event,
                alert == null ? null : alert.getId(),
                incident == null ? null : incident.getId());
    }

    @Transactional(readOnly = true)
    public Page<AlertResponse> listAlerts(Pageable pageable) {
        return alertRepository.findAllByOrderByCreatedAtDesc(pageable).map(AlertResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> listIncidents(Pageable pageable) {
        return incidentRepository.findAllByOrderByCreatedAtDesc(pageable).map(IncidentResponse::from);
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(UUID id, String status) {
        IncidentRecord incident = incidentRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Incident not found"));
        int currentIndex = java.util.List.of("OPEN", "INVESTIGATING", "RESOLVED", "CLOSED")
                .indexOf(incident.getStatus());
        int requestedIndex = java.util.List.of("OPEN", "INVESTIGATING", "RESOLVED", "CLOSED")
                .indexOf(status);
        if (requestedIndex != currentIndex + 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Incident status must advance exactly one lifecycle step");
        }
        incident.setStatus(status);
        incident.setUpdatedAt(Instant.now());
        return IncidentResponse.from(incidentRepository.save(incident));
    }

    @Transactional
    public Map<String, Integer> resetSyntheticData() {
        List<SecurityEvent> syntheticEvents =
                eventRepository.findAllByMessageStartingWith("SIMULATED DEMO EVENT");
        List<UUID> eventIds = syntheticEvents.stream().map(SecurityEvent::getId).toList();
        List<AlertRecord> syntheticAlerts = eventIds.isEmpty()
                ? List.of()
                : alertRepository.findAllByEventIdIn(eventIds);
        List<UUID> alertIds = syntheticAlerts.stream().map(AlertRecord::getId).toList();
        List<IncidentRecord> syntheticIncidents = alertIds.isEmpty()
                ? List.of()
                : incidentRepository.findAllByAlertIdIn(alertIds);

        if (!syntheticIncidents.isEmpty()) {
            incidentRepository.deleteAll(syntheticIncidents);
        }
        if (!syntheticAlerts.isEmpty()) {
            alertRepository.deleteAll(syntheticAlerts);
        }
        if (!syntheticEvents.isEmpty()) {
            eventRepository.deleteAll(syntheticEvents);
        }
        return Map.of(
                "deletedEvents", syntheticEvents.size(),
                "deletedAlerts", syntheticAlerts.size(),
                "deletedIncidents", syntheticIncidents.size());
    }

    private AlertIncidentLink createAlertAndIncident(SecurityEvent event, String rule, int eventCount) {
        String title = rule.replace('_', ' ') + " — " + event.getSourceIp();
        AlertRecord alert = alertRepository.save(new AlertRecord(
                UUID.randomUUID(), title, event.getSeverity(), "OPEN", event.getTimestamp(),
                event.getSourceIp(), eventCount, event.getId(), rule));
        IncidentRecord incident = incidentRepository.save(new IncidentRecord(
                UUID.randomUUID(), title, event.getSeverity(), "OPEN",
                event.getTimestamp(), event.getTimestamp(), alert.getId()));
        return new AlertIncidentLink(alert.getId(), incident.getId());
    }

    private record AlertIncidentLink(UUID alertId, UUID incidentId) {
    }
}
