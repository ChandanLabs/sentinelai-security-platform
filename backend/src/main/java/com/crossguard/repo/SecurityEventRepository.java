package com.crossguard.repo;
import com.crossguard.model.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, UUID> {
    List<SecurityEvent> findTop500ByUserIdOrderByOccurredAtDesc(UUID userId);
    long countByUserIdAndOutcomeAndOccurredAtAfter(UUID userId, String outcome, Instant after);
}
