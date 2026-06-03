package com.ore.backend.product.dto;

import com.ore.backend.product.entity.ProductCategory;
import com.ore.backend.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetProductResponse {

    private UUID productId;
    private String name;
    private String nickname;
    private ProductCategory category;
    private String imageUrl;
    private Boolean isFavorite;
    private Boolean hasRepairHistory;
    private LocalDate purchaseDate;
    private Long remainingDays;
    private LocalDateTime createdAt;

    public static GetProductResponse from(ProductEntity product,
                                          String imageUrl,
                                          boolean hasRepairHistory,
                                          Long remainingDays) {
        return GetProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .nickname(product.getNickname())
                .category(product.getCategory())
                .imageUrl(imageUrl)
                .isFavorite(product.getIsFavorite())
                .hasRepairHistory(hasRepairHistory)
                .purchaseDate(product.getPurchaseDate())
                .remainingDays(remainingDays)
                .createdAt(product.getCreatedAt())
                .build();
    }
}