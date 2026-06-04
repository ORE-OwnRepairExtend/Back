package com.ore.backend.productSource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiOcrRequest {

    private String imageUrl;

    private String sourceType;
}