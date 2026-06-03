package com.ore.backend.product.dto;

import com.ore.backend.productUserManual.entity.ProductUserManualEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductUserManualInfo {

    private String manualContent;

    public static ProductUserManualInfo from(ProductUserManualEntity userManual) {
        return ProductUserManualInfo.builder()
                .manualContent(userManual.getManualContent())
                .build();
    }
}