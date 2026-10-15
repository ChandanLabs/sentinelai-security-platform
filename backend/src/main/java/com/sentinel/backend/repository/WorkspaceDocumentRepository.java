package com.sentinel.backend.repository;

import com.sentinel.backend.model.WorkspaceDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceDocumentRepository extends JpaRepository<WorkspaceDocument, UUID> {
    List<WorkspaceDocument> findAllByOwnerIdOrderByTitle(UUID ownerId);
    Optional<WorkspaceDocument> findByIdAndOwnerId(UUID id, UUID ownerId);
    void deleteAllByOwnerId(UUID ownerId);
}
