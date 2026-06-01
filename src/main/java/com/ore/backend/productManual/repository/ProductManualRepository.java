package com.ore.backend.productManual.repository;

import com.ore.backend.productManual.entity.ProductManualEntity;
import com.ore.backend.productModel.entity.ProductModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductManualRepository extends JpaRepository<ProductManualEntity, UUID> {

    boolean existsByModel(ProductModelEntity model);
}
