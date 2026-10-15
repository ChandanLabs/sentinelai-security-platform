package com.sentinel.backend.repository;

import com.sentinel.backend.model.SecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, UUID> {
    Page<SecurityEvent> findAllByOrderByTimestampDesc(Pageable pageable);
    long countBySeverity(String severity);
    long countByEventTypeAndSourceIpAndTimestampAfter(
            String eventType, String sourceIp, Instant timestamp);
    List<SecurityEvent> findAllByTimestampGreaterThanEqual(Instant timestamp);
    List<SecurityEvent> findAllByMessageStartingWith(String messagePrefix);
}
