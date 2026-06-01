package com.ore.backend.productSource.entity;

import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_sources")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSourceEntity {

    @Id
    @GeneratedValue
    @Column(name = "source_id", nullable = false, updatable = false)
    private UUID sourceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "ocr_text", columnDefinition = "text")
    private String ocrText;

    @Column(name = "model_number", length = 100)
    private String modelNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 50)
    private SourceType sourceType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProductSourceEntity(
            UserEntity user,
            String imageUrl,
            String ocrText,
            String modelNumber,
            SourceType sourceType
    ) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.ocrText = ocrText;
        this.modelNumber = modelNumber;
        this.sourceType = sourceType;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}