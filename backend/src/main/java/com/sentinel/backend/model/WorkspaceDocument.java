package com.sentinel.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "workspace_documents", indexes = {
        @Index(name = "idx_workspace_documents_owner", columnList = "owner_id")
})
public class WorkspaceDocument {
    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String body;

    protected WorkspaceDocument() {
    }

    public WorkspaceDocument(UUID id, UUID ownerId, String title, String body) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.body = body;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
}
