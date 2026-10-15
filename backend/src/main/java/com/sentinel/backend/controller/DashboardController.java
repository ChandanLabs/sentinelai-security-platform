package com.sentinel.backend.controller;

import com.sentinel.backend.model.SecurityEvent;
import com.sentinel.backend.repository.AlertRepository;
import com.sentinel.backend.repository.IncidentRepository;
import com.sentinel.backend.repository.SecurityEventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final SecurityEventRepository events;
    private final AlertRepository alerts;
    private final IncidentRepository incidents;

    public DashboardController(SecurityEventRepository events, AlertRepository alerts,
                               IncidentRepository incidents) {
        this.events = events;
        this.alerts = alerts;
        this.incidents = incidents;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<String> severities = List.of("INFO", "LOW", "MEDIUM", "HIGH", "CRITICAL");
        List<Map<String, Object>> severityBreakdown = severities.stream()
                .map(severity -> Map.<String, Object>of(
                        "severity", severity, "count", events.countBySeverity(severity)))
                .toList();
        Instant start = LocalDate.now(ZoneOffset.UTC).minusDays(6)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        Map<LocalDate, Double> averageRiskByDate = events.findAllByTimestampGreaterThanEqual(start)
                .stream()
                .collect(Collectors.groupingBy(
                        event -> event.getTimestamp().atZone(ZoneOffset.UTC).toLocalDate(),
                        Collectors.averagingInt(SecurityEvent::getRiskScore)));
        List<Map<String, Object>> riskTrend = new ArrayList<>();
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate date = LocalDate.now(ZoneOffset.UTC).minusDays(offset);
            riskTrend.add(Map.of(
                    "date", date.toString(),
                    "score", Math.round(averageRiskByDate.getOrDefault(date, 0.0))));
        }
        return Map.of(
                "totalEvents", events.count(),
                "openAlerts", alerts.countByStatus("OPEN"),
                "activeIncidents", incidents.countByStatusIn(List.of("OPEN", "INVESTIGATING")),
                "criticalEvents", events.countBySeverity("CRITICAL"),
                "riskTrend", riskTrend,
                "severityBreakdown", severityBreakdown);
    }
}
