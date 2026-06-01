package com.ore.backend.productCustomManual.repository;

import com.ore.backend.productCustomManual.entity.ProductCustomManualEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductCustomManualRepository extends JpaRepository<ProductCustomManualEntity, UUID> {

    void deleteByProduct_ProductId(UUID productId);

}