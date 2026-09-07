package com.sentinel.backend.core.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.List;
import java.time.Instant;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findAllByOrderByEventTimestampDesc(Pageable pageable);
    Page<Event> findByEventSourceIdOrderByEventTimestampDesc(UUID eventSourceId, Pageable pageable);

    long countByEventTypeAndSourceIpAndEventTimestampGreaterThanEqual(String eventType, String sourceIp, Instant since);
    long countBySeverityAndSourceIpAndEventTimestampGreaterThanEqual(EventSeverity severity, String sourceIp, Instant since);
}
