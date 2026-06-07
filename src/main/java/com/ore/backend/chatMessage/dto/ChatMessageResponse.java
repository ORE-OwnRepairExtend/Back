package com.ore.backend.chatMessage.dto;

import com.ore.backend.chatMessage.entity.ChatMessageEntity;
import com.ore.backend.chatMessage.entity.ChatMessageRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChatMessageResponse {

    private UUID messageId;
    private ChatMessageRole role;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessageEntity message) {
        return ChatMessageResponse.builder()
                .messageId(message.getMessageId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}