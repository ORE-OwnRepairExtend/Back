package com.ore.backend.global.ai;

import com.ore.backend.chatMessage.dto.AiChatRequest;
import com.ore.backend.chatMessage.dto.AiChatResponse;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.productManual.dto.AiProductManualResponse;
import com.ore.backend.productManual.dto.AiProductManualRequest;
import com.ore.backend.productSource.dto.AiOcrRequest;
import com.ore.backend.productSource.dto.AiOcrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // 제품 이미지 URL을 AI 서버에 전달하여 OCR 분석 요청
    public AiOcrResponse analyzeImage(String imageUrl, String sourceType) {
        try {
            RestClient restClient = restClientBuilder
                    .baseUrl(aiServerUrl)
                    .build();

            return restClient.post()
                    .uri("/ai/ocr/model")
                    .body(new AiOcrRequest(imageUrl, sourceType))
                    .retrieve()
                    .body(AiOcrResponse.class);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_ANALYSIS_FAILED);
        }
    }

    // 제품 정보를 AI 서버에 전달하여 공식 매뉴얼 검색 요청
    public AiProductManualResponse searchManual(
            String brandName,
            String productName,
            String modelNumber
    ) {
        AiProductManualRequest request = new AiProductManualRequest(
                brandName,
                productName,
                modelNumber
        );

        try {
            RestClient restClient = restClientBuilder
                    .baseUrl(aiServerUrl)
                    .build();

            return restClient.post()
                    .uri("/ai/product-manuals/process")
                    .body(request)
                    .retrieve()
                    .body(AiProductManualResponse.class);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.MANUAL_SEARCH_FAILED);
        }
    }

    // 사용자 질문과 제품/매뉴얼 문맥을 AI 서버에 전달하여 답변 생성 요청
    public AiChatResponse generateChatAnswer(AiChatRequest request) {
        try {
            RestClient restClient = restClientBuilder
                    .baseUrl(aiServerUrl)
                    .build();

            AiChatResponse response = restClient.post()
                    .uri("/ai/chat")
                    .body(request)
                    .retrieve()
                    .body(AiChatResponse.class);

            if (response == null || response.getAnswer() == null || response.getAnswer().isBlank()) {
                throw new CustomException(ErrorCode.AI_RESPONSE_FAILED);
            }

            return response;

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_RESPONSE_FAILED);
        }
    }
}