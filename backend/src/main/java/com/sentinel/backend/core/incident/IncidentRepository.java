package com.sentinel.backend.core.incident;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    Page<Incident> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Incident> findByStatusOrderByCreatedAtDesc(IncidentStatus status, Pageable pageable);
}
