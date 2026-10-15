package com.sentinel.backend.repository;

import com.sentinel.backend.model.SecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, UUID> {
    Page<SecurityEvent> findAllByOwnerIdOrderByTimestampDesc(UUID ownerId, Pageable pageable);
    long countByOwnerIdAndSeverity(UUID ownerId, String severity);
    long countByOwnerIdAndEventTypeAndSourceIpAndTimestampAfter(
            UUID ownerId, String eventType, String sourceIp, Instant timestamp);
    long countByOwnerIdAndEventTypeAndUsernameAndTimestampAfter(
            UUID ownerId, String eventType, String username, Instant timestamp);
    List<SecurityEvent> findAllByOwnerIdAndTimestampGreaterThanEqual(UUID ownerId, Instant timestamp);
    java.util.Optional<SecurityEvent> findByIdAndOwnerId(UUID id, UUID ownerId);
    long countByOwnerId(UUID ownerId);
    List<SecurityEvent> findAllByTimestampBefore(Instant timestamp);
    void deleteAllByOwnerId(UUID ownerId);
}
