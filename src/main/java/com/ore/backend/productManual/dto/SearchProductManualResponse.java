package com.ore.backend.productManual.dto;

import com.ore.backend.productManual.entity.ProductManualEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SearchProductManualResponse {

    private UUID manualId;
    private String manualUrl;
    private String manualSummary;
    private String customerCenter;

    public static SearchProductManualResponse from(ProductManualEntity manual) {
        return SearchProductManualResponse.builder()
                .manualId(manual.getManualId())
                .manualUrl(manual.getManualUrl())
                .manualSummary(manual.getManualSummary())
                .customerCenter(manual.getCustomerCenter())
                .build();
    }
}