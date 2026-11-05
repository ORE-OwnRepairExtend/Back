package com.ore.backend.product.repository;

import com.ore.backend.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByUser_UserIdOrderByCreatedAtDesc(UUID userId);

    List<ProductEntity> findByUser_UserIdAndIsFavoriteTrueOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"source", "model"})
    Optional<ProductEntity> findByProductIdAndUser_UserId(UUID productId, UUID userId);

    @EntityGraph(attributePaths = {"source", "model", "user"})
    Optional<ProductEntity> findWithDetailsByProductId(UUID productId);

    List<ProductEntity> findByUser_UserId(UUID userId);

    @EntityGraph(attributePaths = "user")
    List<ProductEntity> findByUser_UserIdInOrderByCreatedAtDesc(Collection<UUID> userIds);
}
