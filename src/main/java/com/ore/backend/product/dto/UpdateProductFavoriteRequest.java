package com.ore.backend.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProductFavoriteRequest {

    @NotNull(message = "즐겨찾기 여부는 필수입니다.")
    private Boolean isFavorite;
}