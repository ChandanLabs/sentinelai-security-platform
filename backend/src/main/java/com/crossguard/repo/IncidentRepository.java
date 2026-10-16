package com.crossguard.repo;
import com.crossguard.model.SecurityIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface IncidentRepository extends JpaRepository<SecurityIncident, UUID> {
    List<SecurityIncident> findTop200ByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<SecurityIncident> findByIdAndUserId(UUID id, UUID userId);
}
