package com.ore.backend.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Owner owner;

    public static GetProductResponse from(ProductEntity product,
                                          String imageUrl,
                                          boolean hasRepairHistory,
                                          Long remainingDays,
                                          Owner owner) {
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
                .owner(owner)
                .build();
    }

    @Getter
    @Builder
    public static class Owner {

        private UUID userId;
        private String name;
        private String nickname;
    }
}
