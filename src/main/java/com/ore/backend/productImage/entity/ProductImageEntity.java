package com.ore.backend.productImage.entity;

import com.ore.backend.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_images",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_images_product_id",
                        columnNames = "product_id"
                )
        }
)
public class ProductImageEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_image_id", nullable = false, updatable = false)
    private UUID productImageId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // S3 object key 저장
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ProductImageEntity(ProductEntity product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.isPrimary = true;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.isPrimary == null) {
            this.isPrimary = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}