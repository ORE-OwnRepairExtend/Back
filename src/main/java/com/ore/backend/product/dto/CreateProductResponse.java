package com.ore.backend.product.dto;

import com.ore.backend.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateProductResponse {

    private UUID productId;
    private String name;
    private String nickname;
    private LocalDateTime createdAt;

    public static CreateProductResponse from(ProductEntity product) {
        return CreateProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .nickname(product.getNickname())
                .createdAt(product.getCreatedAt())
                .build();
    }
}