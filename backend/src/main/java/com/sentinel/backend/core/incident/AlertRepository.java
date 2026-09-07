package com.sentinel.backend.core.incident;

import com.sentinel.backend.core.risk.RiskLevel;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    Optional<Alert> findByEventId(UUID eventId);
    Page<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status, Pageable pageable);
    Page<Alert> findByRiskLevelOrderByCreatedAtDesc(RiskLevel level, Pageable pageable);
    Page<Alert> findByIncidentIdOrderByCreatedAtDesc(UUID incidentId, Pageable pageable);
}
