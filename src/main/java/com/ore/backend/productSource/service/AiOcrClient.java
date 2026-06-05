package com.ore.backend.productSource.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.productSource.dto.AiOcrRequest;
import com.ore.backend.productSource.dto.AiOcrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiOcrClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public AiOcrResponse analyzeImage(String imageUrl, String sourceType) {
        try {
            RestClient restClient = restClientBuilder.build();

            return restClient.post()
                    .uri(aiServerUrl + "/ai/ocr/model")
                    .body(new AiOcrRequest(imageUrl, sourceType))
                    .retrieve()
                    .body(AiOcrResponse.class);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_ANALYSIS_FAILED);
        }
    }
}