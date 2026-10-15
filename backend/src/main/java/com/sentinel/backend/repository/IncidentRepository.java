package com.sentinel.backend.repository;

import com.sentinel.backend.model.IncidentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<IncidentRecord, UUID> {
    Page<IncidentRecord> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByStatusIn(Collection<String> statuses);
    List<IncidentRecord> findAllByAlertIdIn(Collection<UUID> alertIds);
    java.util.Optional<IncidentRecord> findFirstByAlertId(UUID alertId);
}
