package com.sentinel.backend.core.risk;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RiskScoreRepository extends JpaRepository<RiskScore, UUID> {
    Optional<RiskScore> findByEventId(UUID eventId);
    Page<RiskScore> findAllByOrderByCalculatedAtDesc(Pageable pageable);
    Page<RiskScore> findByLevelOrderByCalculatedAtDesc(RiskLevel level, Pageable pageable);
}
