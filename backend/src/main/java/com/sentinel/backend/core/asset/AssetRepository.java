package com.sentinel.backend.core.asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AssetRepository extends JpaRepository<Asset, UUID> { boolean existsByNameIgnoreCase(String name); }
