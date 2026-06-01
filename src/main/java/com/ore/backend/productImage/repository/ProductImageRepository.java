package com.ore.backend.productImage.repository;

import com.ore.backend.productImage.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {

    Optional<ProductImageEntity> findByProduct_ProductId(UUID productId);
}