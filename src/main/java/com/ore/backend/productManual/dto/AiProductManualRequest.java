package com.ore.backend.productManual.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiProductManualRequest {

    private String brandName;
    private String productName;
    private String modelNumber;
}