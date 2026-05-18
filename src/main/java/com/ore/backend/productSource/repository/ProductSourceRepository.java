package com.ore.backend.productSource.repository;

import com.ore.backend.productSource.entity.ProductSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductSourceRepository extends JpaRepository<ProductSourceEntity, UUID> {

    Optional<ProductSourceEntity> findBySourceIdAndUser_UserId(UUID sourceId, UUID userId);
}