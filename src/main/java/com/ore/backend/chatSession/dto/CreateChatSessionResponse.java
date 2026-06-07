package com.ore.backend.chatSession.dto;

import com.ore.backend.chatSession.entity.ChatSessionEntity;
import com.ore.backend.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateChatSessionResponse {

    private UUID sessionId;
    private UUID productId;
    private LocalDateTime createdAt;

    public static CreateChatSessionResponse from(ChatSessionEntity chatSession) {
        ProductEntity product = chatSession.getProduct();

        return CreateChatSessionResponse.builder()
                .sessionId(chatSession.getSessionId())
                .productId(product == null ? null : product.getProductId())
                .createdAt(chatSession.getCreatedAt())
                .build();
    }
}