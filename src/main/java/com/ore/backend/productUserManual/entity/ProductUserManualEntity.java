package com.ore.backend.productUserManual.entity;

import com.ore.backend.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_user_manuals",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_user_manuals_product_id",
                        columnNames = "product_id"
                )
        }
)
public class ProductUserManualEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_manual_id", nullable = false, updatable = false)
    private UUID userManualId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "manual_content", nullable = false, columnDefinition = "text")
    private String manualContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ProductUserManualEntity(ProductEntity product, String manualContent) {
        this.product = product;
        this.manualContent = manualContent;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}