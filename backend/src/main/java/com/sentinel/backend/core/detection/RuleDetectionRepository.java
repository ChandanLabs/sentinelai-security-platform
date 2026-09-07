package com.sentinel.backend.core.detection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RuleDetectionRepository extends JpaRepository<RuleDetection, UUID> {
    boolean existsByEventIdAndRuleCode(UUID eventId, String ruleCode);
    Page<RuleDetection> findAllByOrderByDetectedAtDesc(Pageable pageable);
    Page<RuleDetection> findByEventIdOrderByDetectedAtDesc(UUID eventId, Pageable pageable);
    List<RuleDetection> findByEventIdOrderByDetectedAtDesc(UUID eventId);
}
