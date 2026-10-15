package com.sentinel.backend.service;

import com.sentinel.backend.dto.EventIngestRequest;
import com.sentinel.backend.dto.EventResponse;
import com.sentinel.backend.model.AlertRecord;
import com.sentinel.backend.model.IncidentRecord;
import com.sentinel.backend.model.SecurityEvent;
import com.sentinel.backend.repository.AlertRepository;
import com.sentinel.backend.repository.IncidentRepository;
import com.sentinel.backend.repository.SecurityEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicLong;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityEventServiceTest {
    @Mock
    private SecurityEventRepository eventRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private IncidentRepository incidentRepository;

    private SecurityEventService service;

    @BeforeEach
    void setUp() {
        service = new SecurityEventService(eventRepository, alertRepository, incidentRepository);
    }

    private void echoSavedEvents() {
        when(eventRepository.save(any(SecurityEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void normalLoginIsPersistedWithoutClaimingAiAnalysis() {
        echoSavedEvents();
        EventResponse result = service.ingest(new EventIngestRequest(
                "login_success", "192.0.2.20", "demo-user",
                "SIMULATED DEMO EVENT — Synthetic successful login"));

        assertEquals("INFO", result.severity());
        assertEquals(5, result.riskScore());
        assertEquals("NOT_CONFIGURED", result.aiAnalysisStatus());
        assertEquals("", result.detectionRule());
        verify(eventRepository).save(any(SecurityEvent.class));
        verify(alertRepository, never()).save(any(AlertRecord.class));
        verify(incidentRepository, never()).save(any(IncidentRecord.class));
    }

    @Test
    void fifthFailedLoginFromSameSourceCreatesRuleAlertAndIncident() {
        echoSavedEvents();
        AtomicLong priorFailures = new AtomicLong();
        when(alertRepository.save(any(AlertRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(incidentRepository.save(any(IncidentRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.countByEventTypeAndSourceIpAndTimestampAfter(
                anyString(), anyString(), any()))
                .thenAnswer(invocation -> priorFailures.getAndIncrement());

        EventResponse result = null;
        for (int attempt = 0; attempt < 5; attempt++) {
            result = service.ingest(new EventIngestRequest(
                    "login_failed", "198.51.100.42", "demo-user",
                    "SIMULATED DEMO EVENT — Synthetic failed login"));
        }

        assertEquals("HIGH", result.severity());
        assertEquals(85, result.riskScore());
        assertEquals("AUTH_BRUTE_FORCE", result.detectionRule());
        verify(alertRepository).save(any(AlertRecord.class));
        verify(incidentRepository).save(any(IncidentRecord.class));
    }

    @Test
    void resetDeletesOnlySyntheticEventsAndTheirAlertIncidentRecords() {
        UUID eventId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        SecurityEvent event = new SecurityEvent(
                eventId, "suspicious_command", "HIGH", Instant.now(), "198.51.100.42",
                "demo-user", "SIMULATED DEMO EVENT — test", 78, "SUSPICIOUS_COMMAND",
                "NOT_CONFIGURED");
        AlertRecord alert = new AlertRecord(
                alertId, "Synthetic alert", "HIGH", "OPEN", Instant.now(),
                event.getSourceIp(), 1, eventId, "SUSPICIOUS_COMMAND");
        IncidentRecord incident = new IncidentRecord(
                UUID.randomUUID(), "Synthetic incident", "HIGH", "OPEN",
                Instant.now(), Instant.now(), alertId);
        when(eventRepository.findAllByMessageStartingWith("SIMULATED DEMO EVENT"))
                .thenReturn(List.of(event));
        when(alertRepository.findAllByEventIdIn(List.of(eventId))).thenReturn(List.of(alert));
        when(incidentRepository.findAllByAlertIdIn(List.of(alertId))).thenReturn(List.of(incident));

        Map<String, Integer> result = service.resetSyntheticData();

        assertEquals(Map.of("deletedEvents", 1, "deletedAlerts", 1, "deletedIncidents", 1), result);
        verify(incidentRepository).deleteAll(List.of(incident));
        verify(alertRepository).deleteAll(List.of(alert));
        verify(eventRepository).deleteAll(List.of(event));
    }
}
