package com.ore.backend.productSource.entity;

import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_sources")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductSourceEntity {

    @Id
    @GeneratedValue
    @JdbcType(BinaryJdbcType.class)
    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}