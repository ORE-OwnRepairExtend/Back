package com.ore.backend.productManual.entity;

import com.ore.backend.productModel.entity.ProductModelEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_manuals")
public class ProductManualEntity {

    @Id
    @GeneratedValue
    @Column(name = "manual_id", nullable = false, updatable = false)
    private UUID manualId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ProductModelEntity model;

    @Column(name = "manual_url", length = 500)
    private String manualUrl;

    @Column(name = "manual_summary", columnDefinition = "text")
    private String manualSummary;

    @Column(name = "customer_center", length = 100)
    private String customerCenter;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProductManualEntity(
            ProductModelEntity model,
            String manualUrl,
            String manualSummary,
            String customerCenter
    ) {
        this.model = model;
        this.manualUrl = manualUrl;
        this.manualSummary = manualSummary;
        this.customerCenter = customerCenter;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}