package com.ore.backend.productSource.dto;

import com.ore.backend.productSource.entity.SourceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateProductSourceResponse {

    private UUID sourceId;
    private String imageUrl;
    private String ocrText;
    private String modelNumber;
    private String brandName;
    private String productName;
    private SourceType sourceType;
    private LocalDateTime createdAt;
}