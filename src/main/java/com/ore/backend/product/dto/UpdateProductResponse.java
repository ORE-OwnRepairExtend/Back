package com.ore.backend.product.dto;

import com.ore.backend.product.entity.ProductCategory;
import com.ore.backend.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateProductResponse {

    private UUID productId;
    private String nickname;
    private String imageUrl;
    private ProductCategory category;

    public static UpdateProductResponse from(
            ProductEntity product,
            String imageUrl
    ) {
        return UpdateProductResponse.builder()
                .productId(product.getProductId())
                .nickname(product.getNickname())
                .imageUrl(imageUrl)
                .category(product.getCategory())
                .build();
    }
}