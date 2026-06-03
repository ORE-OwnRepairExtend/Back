package com.ore.backend.repairHistory.entity;

import com.ore.backend.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "repair_histories")
public class RepairHistoryEntity {

    @Id
    @GeneratedValue
    @Column(name = "repair_id", nullable = false, updatable = false)
    private UUID repairId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "service_center", length = 100)
    private String serviceCenter;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public RepairHistoryEntity(
            ProductEntity product,
            String title,
            LocalDate date,
            String content,
            String serviceCenter,
            Integer cost,
            String imageUrl
    ) {
        this.product = product;
        this.title = title;
        this.date = date;
        this.content = content;
        this.serviceCenter = serviceCenter;
        this.cost = cost;
        this.imageUrl = imageUrl;
    }

    public void updateRepairHistory(
            String title,
            LocalDate date,
            String content,
            String serviceCenter,
            Integer cost
    ) {
        if (title != null) {
            this.title = title;
        }

        if (date != null) {
            this.date = date;
        }

        if (content != null) {
            this.content = content;
        }

        if (serviceCenter != null) {
            this.serviceCenter = serviceCenter;
        }

        if (cost != null) {
            this.cost = cost;
        }
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void deleteImage() {
        this.imageUrl = null;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}