package com.ore.backend.productSource.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiOcrResponse {

    private String ocrText;

    private String brandName;

    private String productName;

    private String modelNumber;
}