package com.sentinel.backend.controller;

import com.sentinel.backend.dto.EventResponse;
import com.sentinel.backend.dto.PageResponse;
import com.sentinel.backend.service.SecurityEventService;
import com.sentinel.backend.service.WorkspaceSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final SecurityEventService service;
    private final WorkspaceSessionService sessionService;

    public EventController(SecurityEventService service, WorkspaceSessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @GetMapping
    public PageResponse<EventResponse> list(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(service.listEvents(
                sessionService.requireOwner(session), pageRequest(page, size)));
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable UUID id, HttpSession session) {
        try {
            return service.getEvent(sessionService.requireOwner(session), id);
        } catch (java.util.NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    private PageRequest pageRequest(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "page must be non-negative and size must be 1..100");
        }
        return PageRequest.of(page, size);
    }
}
