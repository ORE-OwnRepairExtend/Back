package com.ore.backend.productModel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_models",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_models_model_number",
                        columnNames = "model_number"
                )
        }
)
public class ProductModelEntity {

    @Id
    @GeneratedValue
    @Column(name = "model_id", nullable = false, updatable = false)
    private UUID modelId;

    @Column(name = "brand_name", length = 100)
    private String brandName;

    @Column(name = "model_number", nullable = false, length = 100)
    private String modelNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProductModelEntity(String brandName, String modelNumber) {
        this.brandName = brandName;
        this.modelNumber = modelNumber;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}