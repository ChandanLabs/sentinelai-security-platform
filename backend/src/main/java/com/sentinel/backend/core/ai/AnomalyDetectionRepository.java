package com.sentinel.backend.core.ai;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnomalyDetectionRepository extends JpaRepository<AnomalyDetection, UUID> {
    Optional<AnomalyDetection> findByEventId(UUID eventId);
    Page<AnomalyDetection> findAllByOrderByDetectedAtDesc(Pageable pageable);
}
