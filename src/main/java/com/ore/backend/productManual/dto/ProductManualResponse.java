package com.ore.backend.productManual.dto;

import com.ore.backend.productManual.entity.ProductManualEntity;
import com.ore.backend.productManual.entity.ProductManualType;
import com.ore.backend.productUserManual.entity.ProductUserManualEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductManualResponse {

    private ProductManualType manualType;

    private String manualContent;

    private String manualUrl;
    private String manualSummary;
    private String customerCenter;

    public static ProductManualResponse from(ProductManualEntity manual) {
        return ProductManualResponse.builder()
                .manualType(ProductManualType.OFFICIAL)
                .manualContent(null)
                .manualUrl(manual.getManualUrl())
                .manualSummary(manual.getManualSummary())
                .customerCenter(manual.getCustomerCenter())
                .build();
    }

    public static ProductManualResponse from(ProductUserManualEntity manual) {
        return ProductManualResponse.builder()
                .manualType(ProductManualType.USER)
                .manualContent(manual.getManualContent())
                .manualUrl(null)
                .manualSummary(null)
                .customerCenter(null)
                .build();
    }
}
