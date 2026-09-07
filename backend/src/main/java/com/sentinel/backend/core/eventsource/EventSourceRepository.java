package com.sentinel.backend.core.eventsource;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface EventSourceRepository extends JpaRepository<EventSource,UUID>{boolean existsByNameIgnoreCaseAndApplicationId(String name,UUID applicationId);}
