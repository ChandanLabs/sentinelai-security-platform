package com.sentinel.backend.controller;

import com.sentinel.backend.dto.WorkspaceDocumentResponse;
import com.sentinel.backend.service.WorkspaceService;
import com.sentinel.backend.service.WorkspaceSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceSessionService sessionService;

    public WorkspaceController(WorkspaceService workspaceService, WorkspaceSessionService sessionService) {
        this.workspaceService = workspaceService;
        this.sessionService = sessionService;
    }

    @GetMapping("/documents")
    public List<WorkspaceDocumentResponse> documents(HttpSession session) {
        return workspaceService.listDocuments(
                sessionService.requireOwner(session), sessionService.requireSessionTag(session));
    }

    @GetMapping("/documents/{id}")
    public WorkspaceDocumentResponse document(@PathVariable UUID id, HttpSession session) {
        WorkspaceDocumentResponse response = workspaceService.readDocument(
                sessionService.requireOwner(session), sessionService.requireSessionTag(session), id);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document is unavailable.");
        }
        return response;
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin")
    public void deniedAdminOperation(
            HttpSession session, @RequestHeader("X-CSRF-Token") String csrfToken) {
        sessionService.requireCsrf(session, csrfToken);
        workspaceService.denyPrivilegedAction(
                sessionService.requireOwner(session), sessionService.requireSessionTag(session));
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrative access is not enabled.");
    }

    @org.springframework.web.bind.annotation.PostMapping("/export")
    public ResponseEntity<String> export(
            HttpSession session, @RequestHeader("X-CSRF-Token") String csrfToken) {
        sessionService.requireCsrf(session, csrfToken);
        UUID ownerId = sessionService.requireOwner(session);
        String csv = workspaceService.exportDocuments(ownerId, sessionService.requireSessionTag(session));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"workspace-documents.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
