package com.ore.backend.chatMessage.service;

import com.ore.backend.chatMessage.dto.AiChatHistory;
import com.ore.backend.chatMessage.dto.AiChatRequest;
import com.ore.backend.chatMessage.dto.AiChatResponse;
import com.ore.backend.chatMessage.dto.AiProductInfo;
import com.ore.backend.chatMessage.dto.ChatMessageResponse;
import com.ore.backend.chatMessage.dto.SendChatMessageRequest;
import com.ore.backend.chatMessage.dto.SendChatMessageResponse;
import com.ore.backend.chatMessage.entity.ChatMessageEntity;
import com.ore.backend.chatMessage.entity.ChatMessageRole;
import com.ore.backend.chatMessage.repository.ChatMessageRepository;
import com.ore.backend.chatSession.entity.ChatSessionEntity;
import com.ore.backend.chatSession.repository.ChatSessionRepository;
import com.ore.backend.global.ai.AiClient;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.productManual.entity.ProductManualEntity;
import com.ore.backend.productUserManual.entity.ProductUserManualEntity;
import com.ore.backend.productManual.repository.ProductManualRepository;
import com.ore.backend.productUserManual.repository.ProductUserManualRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductUserManualRepository productUserManualRepository;
    private final ProductManualRepository productManualRepository;
    private final AiClient aiClient;

    @Transactional
    public SendChatMessageResponse sendMessage(
            UUID userId,
            UUID sessionId,
            SendChatMessageRequest request
    ) {
        // 질문 내용 검증
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new CustomException(ErrorCode.CHAT_MESSAGE_EMPTY);
        }

        // 현재 사용자의 채팅 세션 조회
        ChatSessionEntity chatSession = chatSessionRepository
                .findBySessionIdAndUser_UserId(sessionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_SESSION_NOT_FOUND));

        ProductEntity product = chatSession.getProduct();

        // AI 서버 요청 데이터 구성
        AiChatRequest aiRequest = buildAiChatRequest(
                chatSession,
                product,
                request.getMessage()
        );

        // 사용자 메시지 저장
        ChatMessageEntity userMessage = ChatMessageEntity.create(
                product,
                chatSession,
                ChatMessageRole.USER,
                request.getMessage()
        );

        ChatMessageEntity savedUserMessage = chatMessageRepository.save(userMessage);

        // AI 서버 호출
        AiChatResponse aiResponse = aiClient.generateChatAnswer(aiRequest);
        String answer = aiResponse.getAnswer();

        // AI 메시지 저장
        ChatMessageEntity aiMessage = ChatMessageEntity.create(
                product,
                chatSession,
                ChatMessageRole.ASSISTANT,
                answer
        );

        ChatMessageEntity savedAiMessage = chatMessageRepository.save(aiMessage);

        // 사용자 메시지와 AI 메시지 응답 반환
        return SendChatMessageResponse.builder()
                .userMessage(ChatMessageResponse.from(savedUserMessage))
                .aiMessage(ChatMessageResponse.from(savedAiMessage))
                .build();
    }

    private AiChatRequest buildAiChatRequest(
            ChatSessionEntity chatSession,
            ProductEntity product,
            String message
    ) {
        // 기타 질문 세션의 AI 요청 구성
        if (product == null) {
            return AiChatRequest.builder()
                    .sessionId(chatSession.getSessionId())
                    .message(message)
                    .product(null)
                    .manualContent(null)
                    .manualUrl(null)
                    .history(getRecentHistory(chatSession.getSessionId()))
                    .build();
        }

        // 제품에 연결된 사용자 확정 매뉴얼 조회
        ProductUserManualEntity userManual = productUserManualRepository
                .findByProduct_ProductId(product.getProductId())
                .orElse(null);

        // 제품 모델에 연결된 공식 매뉴얼 URL 조회
        String manualUrl = getManualUrl(product);

        // 제품 질문 세션의 AI 요청 구성
        return AiChatRequest.builder()
                .sessionId(chatSession.getSessionId())
                .message(message)
                .product(toAiProductInfo(product))
                .manualContent(userManual == null ? null : userManual.getManualContent())
                .manualUrl(manualUrl)
                .history(getRecentHistory(chatSession.getSessionId()))
                .build();
    }

    private String getManualUrl(ProductEntity product) {
        if (product.getModel() == null) {
            return null;
        }

        return productManualRepository
                .findByModel_ModelId(product.getModel().getModelId())
                .map(ProductManualEntity::getManualUrl)
                .orElse(null);
    }

    private AiProductInfo toAiProductInfo(ProductEntity product) {
        return AiProductInfo.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .nickname(product.getNickname())
                .category(product.getCategory().name())
                .build();
    }

    private List<AiChatHistory> getRecentHistory(UUID sessionId) {
        // 최근 메시지 10개 조회
        List<ChatMessageEntity> recentMessages =
                chatMessageRepository.findTop10BySession_SessionIdOrderByCreatedAtDesc(sessionId);

        // 시간순 정렬을 위해 역순 변환
        Collections.reverse(recentMessages);

        // AI 서버 history 형식으로 변환
        return recentMessages.stream()
                .map(message -> AiChatHistory.builder()
                        .role(message.getRole())
                        .content(message.getContent())
                        .build())
                .toList();
    }
}