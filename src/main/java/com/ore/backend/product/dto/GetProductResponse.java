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
    private LocalDateTime createdAt;

    public static GetProductResponse from(ProductEntity product,
                                          String imageUrl,
                                          boolean hasRepairHistory) {
        return GetProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .nickname(product.getNickname())
                .category(product.getCategory())
                .imageUrl(imageUrl)
                .isFavorite(product.getIsFavorite())
                .hasRepairHistory(hasRepairHistory) // repair_histories 테이블 구현하고 계산
                .purchaseDate(product.getPurchaseDate())
                .createdAt(product.getCreatedAt())
                .build();
    }
}