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
public class GetProductDetailResponse {

    private UUID productId;
    private String productName;
    private String nickname;
    private ProductCategory category;
    private String imageUrl;
    private String modelNumber;
    private LocalDate purchaseDate;
    private Integer warrantyMonths;
    private Boolean isFavorite;
    private Boolean hasRepairHistory;
    private LocalDateTime createdAt;

    public static GetProductDetailResponse from(
            ProductEntity product,
            String imageUrl,
            boolean hasRepairHistory
    ) {
        return GetProductDetailResponse.builder()
                .productId(product.getProductId())
                .productName(product.getName())
                .nickname(product.getNickname())
                .category(product.getCategory())
                .imageUrl(imageUrl)
                .modelNumber(resolveModelNumber(product))
                .purchaseDate(product.getPurchaseDate())
                .warrantyMonths(product.getWarrantyMonth())
                .isFavorite(product.getIsFavorite())
                .hasRepairHistory(false) // repair_histories 테이블 구현하고 계산
                .createdAt(product.getCreatedAt())
                .build();
    }

    private static String resolveModelNumber(ProductEntity product) {
        if (product.getModel() != null) {
            return product.getModel().getModelNumber();
        }

        if (product.getSource() != null) {
            return product.getSource().getModelNumber();
        }

        return null;
    }
}