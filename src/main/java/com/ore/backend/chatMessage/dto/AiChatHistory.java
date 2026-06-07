package com.ore.backend.chatMessage.dto;

import com.ore.backend.chatMessage.entity.ChatMessageRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiChatHistory {

    private ChatMessageRole role;
    private String content;
}