package com.ore.backend.productModel.repository;

import com.ore.backend.productModel.entity.ProductModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductModelRepository extends JpaRepository<ProductModelEntity, UUID> {

    Optional<ProductModelEntity> findByModelNumber(String modelNumber);
}