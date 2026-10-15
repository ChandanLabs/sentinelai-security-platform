package com.sentinel.backend.controller;

import com.sentinel.backend.dto.AlertResponse;
import com.sentinel.backend.dto.PageResponse;
import com.sentinel.backend.service.SecurityEventService;
import com.sentinel.backend.service.WorkspaceSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {
    private final SecurityEventService service;
    private final WorkspaceSessionService sessionService;

    public AlertController(SecurityEventService service, WorkspaceSessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @GetMapping
    public PageResponse<AlertResponse> list(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "page must be non-negative and size must be 1..100");
        }
        return PageResponse.from(service.listAlerts(
                sessionService.requireOwner(session), PageRequest.of(page, size)));
    }
}
