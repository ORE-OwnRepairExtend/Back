package com.ore.backend.chatSession.dto;

import com.ore.backend.chatSession.entity.ChatSessionEntity;
import com.ore.backend.chatMessage.entity.ChatMessageEntity;
import com.ore.backend.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetChatSessionResponse {

    private UUID sessionId;
    private UUID productId;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;

    public static GetChatSessionResponse of(
            ChatSessionEntity chatSession,
            ChatMessageEntity lastMessage
    ) {
        ProductEntity product = chatSession.getProduct();

        return GetChatSessionResponse.builder()
                .sessionId(chatSession.getSessionId())
                .productId(product == null ? null : product.getProductId())
                .lastMessage(lastMessage == null ? null : lastMessage.getContent())
                .lastMessageAt(lastMessage == null ? null : lastMessage.getCreatedAt())
                .createdAt(chatSession.getCreatedAt())
                .build();
    }
}