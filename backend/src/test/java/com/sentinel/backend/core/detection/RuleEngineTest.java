package com.sentinel.backend.core.detection;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.event.EventRepository;
import com.sentinel.backend.core.event.EventSeverity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleEngineTest {
    @Mock
    private EventRepository events;

    @InjectMocks
    private RuleEngine ruleEngine;

    @Test
    void detectsBruteForceAfterFiveFailedAuthenticationEvents() {
        Event event = event("login_failed", EventSeverity.MEDIUM, "10.0.0.5", "Login failed");
        when(events.countByEventTypeAndSourceIpAndEventTimestampGreaterThanEqual(
                "login_failed", "10.0.0.5", event.getEventTimestamp().minusSeconds(300)))
                .thenReturn(5L);

        List<RuleMatch> matches = ruleEngine.evaluate(event);

        assertTrue(matches.stream().anyMatch(m -> m.code().equals("AUTH_BRUTE_FORCE")));
    }

    @Test
    void detectsPrivilegeEscalation() {
        Event event = event("role_change", EventSeverity.HIGH, null, "User added to administrator group");

        List<RuleMatch> matches = ruleEngine.evaluate(event);

        assertTrue(matches.stream().anyMatch(m -> m.code().equals("PRIVILEGE_ESCALATION")));
    }

    @Test
    void detectsSuspiciousCommand() {
        Event event = event("process_execution", EventSeverity.HIGH, null, "powershell -enc SGVsbG8=");

        List<RuleMatch> matches = ruleEngine.evaluate(event);

        assertTrue(matches.stream().anyMatch(m -> m.code().equals("SUSPICIOUS_COMMAND")));
    }

    @Test
    void detectsCriticalBurst() {
        Event event = event("security_event", EventSeverity.CRITICAL, "10.0.0.7", "Critical security event");
        when(events.countBySeverityAndSourceIpAndEventTimestampGreaterThanEqual(
                EventSeverity.CRITICAL, "10.0.0.7", event.getEventTimestamp().minusSeconds(600)))
                .thenReturn(3L);

        List<RuleMatch> matches = ruleEngine.evaluate(event);

        assertTrue(matches.stream().anyMatch(m -> m.code().equals("CRITICAL_EVENT_BURST")));
    }

    private Event event(String type, EventSeverity severity, String ip, String message) {
        Event event = new Event();
        event.setEventType(type);
        event.setSeverity(severity);
        event.setEventTimestamp(Instant.parse("2026-01-01T12:00:00Z"));
        event.setSourceIp(ip);
        event.setMessage(message);
        return event;
    }
}
