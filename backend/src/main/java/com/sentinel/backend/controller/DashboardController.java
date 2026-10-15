package com.sentinel.backend.controller;

import com.sentinel.backend.service.SecurityEventService;
import com.sentinel.backend.service.WorkspaceSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final SecurityEventService eventService;
    private final WorkspaceSessionService sessionService;

    public DashboardController(
            SecurityEventService eventService, WorkspaceSessionService sessionService) {
        this.eventService = eventService;
        this.sessionService = sessionService;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(HttpSession session) {
        return eventService.dashboardStats(sessionService.requireOwner(session));
    }
}
