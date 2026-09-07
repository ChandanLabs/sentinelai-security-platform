package com.sentinel.backend.core.detection;

import com.sentinel.backend.core.event.Event;
import com.sentinel.backend.core.event.EventRepository;
import com.sentinel.backend.core.event.EventSeverity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class RuleEngine {
    private static final int BRUTE_FORCE_THRESHOLD = 5;
    private static final int CRITICAL_BURST_THRESHOLD = 3;

    private final EventRepository events;

    public RuleEngine(EventRepository events) {
        this.events = events;
    }

    public List<RuleMatch> evaluate(Event event) {
        List<RuleMatch> matches = new ArrayList<>();
        String type = event.getEventType().toLowerCase(Locale.ROOT);
        String message = event.getMessage().toLowerCase(Locale.ROOT);

        if (isFailedAuthentication(type) && event.getSourceIp() != null) {
            Instant since = event.getEventTimestamp().minus(Duration.ofMinutes(5));
            long count = events.countByEventTypeAndSourceIpAndEventTimestampGreaterThanEqual(
                    event.getEventType(), event.getSourceIp(), since);
            if (count >= BRUTE_FORCE_THRESHOLD) {
                matches.add(new RuleMatch(
                        "AUTH_BRUTE_FORCE",
                        "Repeated failed authentication",
                        EventSeverity.HIGH,
                        "At least " + BRUTE_FORCE_THRESHOLD + " failed authentication events from source IP "
                                + event.getSourceIp() + " were observed within 5 minutes."
                ));
            }
        }

        if (event.getSeverity() == EventSeverity.CRITICAL && event.getSourceIp() != null) {
            Instant since = event.getEventTimestamp().minus(Duration.ofMinutes(10));
            long count = events.countBySeverityAndSourceIpAndEventTimestampGreaterThanEqual(
                    EventSeverity.CRITICAL, event.getSourceIp(), since);
            if (count >= CRITICAL_BURST_THRESHOLD) {
                matches.add(new RuleMatch(
                        "CRITICAL_EVENT_BURST",
                        "Critical event burst",
                        EventSeverity.CRITICAL,
                        "At least " + CRITICAL_BURST_THRESHOLD + " critical events from source IP "
                                + event.getSourceIp() + " were observed within 10 minutes."
                ));
            }
        }

        if (isPrivilegeEscalation(type, message)) {
            matches.add(new RuleMatch(
                    "PRIVILEGE_ESCALATION",
                    "Privilege escalation activity",
                    EventSeverity.CRITICAL,
                    "The event indicates a privilege, role, administrator, root, or sudo change."
            ));
        }

        if (containsSuspiciousCommand(message)) {
            matches.add(new RuleMatch(
                    "SUSPICIOUS_COMMAND",
                    "Suspicious command pattern",
                    EventSeverity.HIGH,
                    "The event message contains a command pattern commonly associated with suspicious execution."
            ));
        }

        return matches;
    }

    private boolean isFailedAuthentication(String type) {
        return type.equals("login_failed") || type.equals("authentication_failed") || type.equals("auth_failed");
    }

    private boolean isPrivilegeEscalation(String type, String message) {
        return type.contains("privilege") || type.contains("role_change") || type.contains("admin_role_change")
                || type.contains("root_access") || message.contains("privilege escalation")
                || message.contains("added to administrator") || message.contains("added to admin")
                || message.contains("sudoers") || message.contains("root access");
    }

    private boolean containsSuspiciousCommand(String message) {
        return message.contains("rm -rf") || message.contains("powershell -enc")
                || message.contains("powershell -e ") || message.contains("cmd /c")
                || message.contains("curl ") && message.contains("| sh")
                || message.contains("wget ") && message.contains("| sh");
    }
}
