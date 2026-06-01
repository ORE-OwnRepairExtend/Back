package com.ore.backend.product.repository;

import com.ore.backend.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    // source를 함께 조회하여 응답의 imageUrl을 사용할 수 있도록 함
    @EntityGraph(attributePaths = {"source"})
    List<ProductEntity> findByUser_UserIdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"source"})
    List<ProductEntity> findByUser_UserIdAndIsFavoriteTrueOrderByCreatedAtDesc(UUID userId);
}
