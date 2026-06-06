package com.ore.backend.productManual.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiProductManualResponse {

    private String manualUrl;
    private String manualSummary;
    private String customerCenter;
}