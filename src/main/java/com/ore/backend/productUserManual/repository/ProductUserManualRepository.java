package com.ore.backend.productUserManual.repository;

import com.ore.backend.productUserManual.entity.ProductUserManualEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductUserManualRepository extends JpaRepository<ProductUserManualEntity, UUID> {

    void deleteByProduct_ProductId(UUID productId);

    Optional<ProductUserManualEntity> findByProduct_ProductId(UUID productId);
}