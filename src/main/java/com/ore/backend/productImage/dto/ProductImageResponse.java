package com.ore.backend.productImage.dto;

import com.ore.backend.productImage.entity.ProductImageEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProductImageResponse {

    private UUID productImageId;
    private String imageUrl;
    private Boolean isPrimary;
    private LocalDateTime createdAt;

    public static ProductImageResponse of(ProductImageEntity productImage, String imageUrl) {
        return ProductImageResponse.builder()
                .productImageId(productImage.getProductImageId())
                .imageUrl(imageUrl)
                .isPrimary(productImage.getIsPrimary())
                .createdAt(productImage.getCreatedAt())
                .build();
    }
}