package com.sentinel.backend.service;

import com.sentinel.backend.dto.WorkspaceDocumentResponse;
import com.sentinel.backend.model.WorkspaceDocument;
import com.sentinel.backend.repository.WorkspaceDocumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {
    private final WorkspaceDocumentRepository documentRepository;
    private final SecurityEventService eventService;

    public WorkspaceService(WorkspaceDocumentRepository documentRepository, SecurityEventService eventService) {
        this.documentRepository = documentRepository;
        this.eventService = eventService;
    }

    @Transactional
    public List<WorkspaceDocumentResponse> listDocuments(UUID ownerId, String sessionTag) {
        eventService.recordActivity(ownerId, "resource_list", "not-collected",
                WorkspaceAuthService.actorId(ownerId), sessionTag, "/workspace/documents", "ALLOWED");
        return documentRepository.findAllByOwnerIdOrderByTitle(ownerId).stream()
                .map(WorkspaceDocumentResponse::from)
                .toList();
    }

    @Transactional
    public WorkspaceDocumentResponse readDocument(
            UUID ownerId, String sessionTag, UUID documentId) {
        WorkspaceDocument document = documentRepository.findByIdAndOwnerId(documentId, ownerId)
                .orElse(null);
        if (document == null) {
            eventService.recordActivity(ownerId, "access_denied", "not-collected",
                    WorkspaceAuthService.actorId(ownerId), sessionTag,
                    "/workspace/documents/" + documentId, "DENIED");
            return null;
        }
        eventService.recordActivity(ownerId, "resource_view", "not-collected",
                WorkspaceAuthService.actorId(ownerId), sessionTag,
                "/workspace/documents/" + documentId, "ALLOWED");
        return WorkspaceDocumentResponse.from(document);
    }

    @Transactional
    public void denyPrivilegedAction(UUID ownerId, String sessionTag) {
        eventService.recordActivity(ownerId, "privileged_access_denied", "not-collected",
                WorkspaceAuthService.actorId(ownerId), sessionTag,
                "/workspace/admin", "DENIED");
    }

    @Transactional
    public String exportDocuments(UUID ownerId, String sessionTag) {
        List<WorkspaceDocument> documents = documentRepository.findAllByOwnerIdOrderByTitle(ownerId);
        eventService.recordActivity(ownerId, "data_export", "not-collected",
                WorkspaceAuthService.actorId(ownerId), sessionTag,
                "/workspace/export", "ALLOWED");
        StringBuilder csv = new StringBuilder("id,title\n");
        for (WorkspaceDocument document : documents) {
            csv.append(document.getId()).append(",\"")
                    .append(document.getTitle().replace("\"", "\"\"")).append("\"\n");
        }
        return csv.toString();
    }
}
