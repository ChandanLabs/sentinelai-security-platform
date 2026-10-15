package com.sentinel.backend.dto;

import com.sentinel.backend.model.WorkspaceDocument;

import java.util.UUID;

public record WorkspaceDocumentResponse(UUID id, String title, String body) {
    public static WorkspaceDocumentResponse from(WorkspaceDocument document) {
        return new WorkspaceDocumentResponse(document.getId(), document.getTitle(), document.getBody());
    }
}
