package com.sentinel.backend.repository;

import com.sentinel.backend.model.IncidentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<IncidentRecord, UUID> {
    Page<IncidentRecord> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId, Pageable pageable);
    long countByOwnerIdAndStatusIn(UUID ownerId, Collection<String> statuses);
    List<IncidentRecord> findAllByAlertIdIn(Collection<UUID> alertIds);
    java.util.Optional<IncidentRecord> findFirstByAlertId(UUID alertId);
    java.util.Optional<IncidentRecord> findByIdAndOwnerId(UUID id, UUID ownerId);
    List<IncidentRecord> findAllByOwnerIdAndCreatedAtBefore(UUID ownerId, java.time.Instant timestamp);
    void deleteAllByOwnerId(UUID ownerId);
}
