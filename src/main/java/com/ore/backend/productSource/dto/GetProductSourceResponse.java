package com.ore.backend.productSource.dto;

import com.ore.backend.productSource.entity.SourceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetProductSourceResponse {

    private UUID sourceId;
    private String imageUrl;
    private String ocrText;
    private String modelNumber;
    private SourceType sourceType;
    private LocalDateTime createdAt;
}