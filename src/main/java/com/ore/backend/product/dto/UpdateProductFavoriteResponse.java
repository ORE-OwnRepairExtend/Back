package com.ore.backend.product.dto;

import com.ore.backend.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateProductFavoriteResponse {

    private UUID productId;
    private Boolean isFavorite;

    public static UpdateProductFavoriteResponse from(ProductEntity product) {
        return UpdateProductFavoriteResponse.builder()
                .productId(product.getProductId())
                .isFavorite(product.getIsFavorite())
                .build();
    }
}