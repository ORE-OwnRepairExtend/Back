package com.ore.backend.product.entity;

import com.ore.backend.productModel.entity.ProductModelEntity;
import com.ore.backend.productSource.entity.ProductSourceEntity;
import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private ProductModelEntity model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private ProductSourceEntity source;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private ProductCategory category;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_month")
    private Integer warrantyMonth;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ProductEntity(
            UserEntity user,
            ProductModelEntity model,
            ProductSourceEntity source,
            String name,
            String nickname,
            ProductCategory category,
            LocalDate purchaseDate,
            Integer warrantyMonth
    ) {
        this.user = user;
        this.model = model;
        this.source = source;
        this.name = name;
        this.nickname = nickname;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.warrantyMonth = warrantyMonth;
        this.isFavorite = false;
    }

    public void updateProductInfo(String nickname, ProductCategory category) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (category != null) {
            this.category = category;
        }
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.isFavorite == null) {
            this.isFavorite = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}