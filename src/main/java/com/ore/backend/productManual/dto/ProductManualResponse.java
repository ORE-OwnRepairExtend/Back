package com.ore.backend.productManual.dto;

import com.ore.backend.productManual.entity.ProductManualEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductManualResponse {

    private String manualUrl;
    private String manualSummary;
    private String customerCenter;

    public static ProductManualResponse from(ProductManualEntity manual) {
        return ProductManualResponse.builder()
                .manualUrl(manual.getManualUrl())
                .manualSummary(manual.getManualSummary())
                .customerCenter(manual.getCustomerCenter())
                .build();
    }
}