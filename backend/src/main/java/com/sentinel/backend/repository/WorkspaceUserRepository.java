package com.sentinel.backend.repository;

import com.sentinel.backend.model.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, UUID> {
    Optional<WorkspaceUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
