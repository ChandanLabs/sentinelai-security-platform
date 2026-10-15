package com.sentinel.backend.service;

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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        when(eventRepository.save(any(SecurityEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void fifthFailedLoginAgainstTheSameAccountCreatesExplainableAlert() {
        UUID ownerId = UUID.randomUUID();
        AtomicLong priorFailures = new AtomicLong();
        when(eventRepository.countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
                any(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> priorFailures.getAndIncrement());
        when(alertRepository.save(any(AlertRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(incidentRepository.save(any(IncidentRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse result = null;
        for (int attempt = 0; attempt < 5; attempt++) {
            result = service.recordActivity(
                    ownerId, "login_failed", "not-collected", "user-demo", null,
                    "/auth/login", "DENIED");
        }

        assertEquals("HIGH", result.severity());
        assertEquals(75, result.riskScore());
        assertEquals("AUTH_BRUTE_FORCE", result.detectionRule());
        assertEquals("5 failed sign-in attempts against this account in five minutes.",
                result.detectionEvidence());
        verify(alertRepository).save(any(AlertRecord.class));
        verify(incidentRepository).save(any(IncidentRecord.class));
    }

    @Test
    void threeDeniedResourceRequestsFromOneAccountTriggerASeparateRule() {
        UUID ownerId = UUID.randomUUID();
        AtomicLong priorDenials = new AtomicLong();
        when(eventRepository.countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
                any(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> priorDenials.getAndIncrement());
        when(alertRepository.save(any(AlertRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(incidentRepository.save(any(IncidentRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse result = null;
        for (int attempt = 0; attempt < 3; attempt++) {
            result = service.recordActivity(
                    ownerId, "access_denied", "not-collected", "user-demo", "session-demo",
                    "/workspace/documents/not-owned", "DENIED");
        }

        assertEquals("REPEATED_DENIED_ACCESS", result.detectionRule());
        assertTrue(result.detectionEvidence().startsWith("3 denied resource requests"));
        verify(alertRepository).save(any(AlertRecord.class));
        verify(incidentRepository).save(any(IncidentRecord.class));
    }

    @Test
    void normalWorkspaceReadDoesNotGenerateAlertAndDoesNotClaimAiAnalysis() {
        UUID ownerId = UUID.randomUUID();

        EventResponse result = service.recordActivity(
                ownerId, "resource_view", "not-collected", "user-demo", "session-demo",
                "/workspace/documents/sample", "ALLOWED");

        assertEquals("INFO", result.severity());
        assertEquals(0, result.riskScore());
        assertEquals("NOT_CONFIGURED", result.aiAnalysisStatus());
        assertEquals("", result.detectionRule());
        assertEquals("ALLOWED", result.outcome());
        verify(alertRepository, never()).save(any(AlertRecord.class));
        verify(incidentRepository, never()).save(any(IncidentRecord.class));
    }
}
