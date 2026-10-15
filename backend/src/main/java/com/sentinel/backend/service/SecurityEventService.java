package com.sentinel.backend.service;

import com.sentinel.backend.dto.AlertResponse;
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
    public synchronized EventResponse recordActivity(
            UUID ownerId, String eventType, String sourceIp, String actorId,
            String sessionTag, String target, String outcome) {
        Instant now = Instant.now();
        String rule = "";
        String severity = "INFO";
        String evidence = "";
        int relatedEvents = 1;

        if ("login_failed".equals(eventType)) {
            relatedEvents = Math.toIntExact(eventRepository
                    .countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
                            ownerId, eventType, actorId, now.minus(5, ChronoUnit.MINUTES)) + 1);
            if (relatedEvents >= 5) {
                rule = "AUTH_BRUTE_FORCE";
                severity = "HIGH";
                evidence = relatedEvents + " failed sign-in attempts against this account in five minutes.";
            } else {
                severity = "MEDIUM";
            }
        } else if ("login_success".equals(eventType)) {
            long failures = eventRepository.countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
                    ownerId, "login_failed", actorId, now.minus(5, ChronoUnit.MINUTES));
            if (failures > 0) {
                rule = "FAILED_THEN_SUCCESS";
                severity = "HIGH";
                relatedEvents = Math.toIntExact(failures + 1);
                evidence = failures + " failed sign-in attempt(s) against this account preceded a successful sign-in within five minutes.";
            }
        } else if ("access_denied".equals(eventType)) {
            relatedEvents = Math.toIntExact(eventRepository
                    .countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
                            ownerId, eventType, actorId, now.minus(5, ChronoUnit.MINUTES)) + 1);
            if (relatedEvents >= 3) {
                rule = "REPEATED_DENIED_ACCESS";
                severity = "HIGH";
                evidence = relatedEvents + " denied resource requests by this account in five minutes.";
            } else {
                severity = "MEDIUM";
            }
        } else if ("privileged_access_denied".equals(eventType)) {
            rule = "PRIVILEGED_ACCESS_DENIED";
            severity = "HIGH";
            evidence = "A standard workspace account requested an administrative operation; the server denied it.";
        } else if ("data_export".equals(eventType)) {
            relatedEvents = Math.toIntExact(eventRepository
                    .countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
                            ownerId, eventType, actorId, now.minus(10, ChronoUnit.MINUTES)) + 1);
            if (relatedEvents >= 3) {
                rule = "REPEATED_DATA_EXPORT";
                severity = "MEDIUM";
                evidence = relatedEvents + " export requests by this account in ten minutes.";
            } else {
                severity = "LOW";
            }
        }

        SecurityEvent event = eventRepository.save(new SecurityEvent(
                UUID.randomUUID(), ownerId, eventType, severity, now, sourceIp, actorId,
                sessionTag, target, outcome, activityMessage(eventType, outcome),
                riskScore(severity), rule, evidence, "NOT_CONFIGURED"));
        UUID alertId = null;
        UUID incidentId = null;
        if (!rule.isEmpty()) {
            AlertIncidentLink link = createOrUpdateAlertIncident(event, ownerId, rule, relatedEvents);
            alertId = link.alertId();
            incidentId = link.incidentId();
        }
        return EventResponse.from(event, alertId, incidentId);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> listEvents(UUID ownerId, Pageable pageable) {
        return eventRepository.findAllByOwnerIdOrderByTimestampDesc(ownerId, pageable)
                .map(EventResponse::from);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID ownerId, UUID id) {
        SecurityEvent event = eventRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Event not found"));
        AlertRecord alert = alertRepository.findFirstByEventIdOrderByCreatedAtDesc(id).orElse(null);
        IncidentRecord incident = alert == null
                ? null : incidentRepository.findFirstByAlertId(alert.getId()).orElse(null);
        return EventResponse.from(event, alert == null ? null : alert.getId(),
                incident == null ? null : incident.getId());
    }

    @Transactional(readOnly = true)
    public Page<AlertResponse> listAlerts(UUID ownerId, Pageable pageable) {
        return alertRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId, pageable)
                .map(AlertResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> listIncidents(UUID ownerId, Pageable pageable) {
        return incidentRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId, pageable)
                .map(IncidentResponse::from);
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(
            UUID ownerId, String sessionTag, UUID id, String status) {
        IncidentRecord incident = incidentRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Incident not found"));
        List<String> lifecycle = List.of("OPEN", "INVESTIGATING", "RESOLVED", "CLOSED");
        int currentIndex = lifecycle.indexOf(incident.getStatus());
        int requestedIndex = lifecycle.indexOf(status);
        if (requestedIndex != currentIndex + 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Incident status must advance exactly one lifecycle step");
        }
        incident.setStatus(status);
        incident.setUpdatedAt(Instant.now());
        IncidentRecord saved = incidentRepository.save(incident);
        recordActivity(ownerId, "incident_status_changed", "not-collected",
                WorkspaceAuthService.actorId(ownerId), sessionTag,
                "/incidents/" + id, "ALLOWED");
        return IncidentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> dashboardStats(UUID ownerId) {
        List<String> severities = List.of("INFO", "LOW", "MEDIUM", "HIGH", "CRITICAL");
        List<Map<String, Object>> breakdown = severities.stream()
                .map(value -> Map.<String, Object>of(
                        "severity", value,
                        "count", eventRepository.countByOwnerIdAndSeverity(ownerId, value)))
                .toList();
        Instant start = Instant.now().minus(6, ChronoUnit.DAYS);
        List<Map<String, Object>> trend = eventRepository
                .findAllByOwnerIdAndTimestampGreaterThanEqual(ownerId, start)
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        event -> event.getTimestamp().truncatedTo(ChronoUnit.DAYS),
                        java.util.stream.Collectors.averagingInt(SecurityEvent::getRiskScore)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> Map.<String, Object>of(
                        "date", entry.getKey().toString(),
                        "score", Math.round(entry.getValue())))
                .toList();
        return Map.of(
                "totalEvents", eventRepository.countByOwnerId(ownerId),
                "openAlerts", alertRepository.countByOwnerIdAndStatus(ownerId, "OPEN"),
                "activeIncidents", incidentRepository.countByOwnerIdAndStatusIn(
                        ownerId, List.of("OPEN", "INVESTIGATING")),
                "criticalEvents", eventRepository.countByOwnerIdAndSeverity(ownerId, "CRITICAL"),
                "riskTrend", trend,
                "severityBreakdown", breakdown);
    }

    @Transactional
    public void recordLoginFailure(UUID ownerId, String sourceIp, String actorId) {
        if (ownerId != null) {
            recordActivity(ownerId, "login_failed", sourceIp, actorId, null, "/auth/login", "DENIED");
        }
    }

    @Transactional
    public void purgeExpiredData(int retentionDays) {
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        List<SecurityEvent> oldEvents = eventRepository.findAllByTimestampBefore(cutoff);
        List<UUID> eventIds = oldEvents.stream().map(SecurityEvent::getId).toList();
        List<AlertRecord> oldAlerts = eventIds.isEmpty()
                ? List.of() : alertRepository.findAllByEventIdIn(eventIds);
        List<UUID> alertIds = oldAlerts.stream().map(AlertRecord::getId).toList();
        List<IncidentRecord> oldIncidents = alertIds.isEmpty()
                ? List.of() : incidentRepository.findAllByAlertIdIn(alertIds);
        incidentRepository.deleteAll(oldIncidents);
        alertRepository.deleteAll(oldAlerts);
        eventRepository.deleteAll(oldEvents);
    }

    @Transactional
    public void deleteOwnerData(UUID ownerId) {
        incidentRepository.deleteAllByOwnerId(ownerId);
        alertRepository.deleteAllByOwnerId(ownerId);
        eventRepository.deleteAllByOwnerId(ownerId);
    }

    private AlertIncidentLink createOrUpdateAlertIncident(
            SecurityEvent event, UUID ownerId, String rule, int eventCount) {
        AlertRecord existing = alertRepository
                .findFirstByOwnerIdAndDetectionRuleAndStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                        ownerId, rule, "OPEN", Instant.now().minus(10, ChronoUnit.MINUTES))
                .orElse(null);
        if (existing != null) {
            existing.setEventCount(existing.getEventCount() + 1);
            alertRepository.save(existing);
            IncidentRecord incident = incidentRepository.findFirstByAlertId(existing.getId()).orElse(null);
            return new AlertIncidentLink(
                    existing.getId(), incident == null ? null : incident.getId());
        }
        String title = rule.replace('_', ' ') + " — " + event.getTargetResource();
        AlertRecord alert = alertRepository.save(new AlertRecord(
                UUID.randomUUID(), title, event.getSeverity(), "OPEN", event.getTimestamp(),
                event.getSourceIp(), eventCount, event.getId(), rule, ownerId));
        IncidentRecord incident = incidentRepository.save(new IncidentRecord(
                UUID.randomUUID(), title, event.getSeverity(), "OPEN",
                event.getTimestamp(), event.getTimestamp(), alert.getId(), ownerId));
        return new AlertIncidentLink(alert.getId(), incident.getId());
    }

    private static int riskScore(String severity) {
        return switch (severity) {
            case "LOW" -> 25;
            case "MEDIUM" -> 50;
            case "HIGH" -> 75;
            case "CRITICAL" -> 100;
            default -> 0;
        };
    }

    private static String activityMessage(String eventType, String outcome) {
        return eventType.replace('_', ' ') + " " + outcome.toLowerCase(java.util.Locale.ROOT);
    }

    private record AlertIncidentLink(UUID alertId, UUID incidentId) {
    }
}
