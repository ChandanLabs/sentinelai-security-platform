package com.sentinel.backend.repository;

import com.sentinel.backend.model.AlertRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Collection;
import java.util.List;

public interface AlertRepository extends JpaRepository<AlertRecord, UUID> {
    Page<AlertRecord> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByStatus(String status);
    List<AlertRecord> findAllByEventIdIn(Collection<UUID> eventIds);
    java.util.Optional<AlertRecord> findFirstByEventIdOrderByCreatedAtDesc(UUID eventId);
}
