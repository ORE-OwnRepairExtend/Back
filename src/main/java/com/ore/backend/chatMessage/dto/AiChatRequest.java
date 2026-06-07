package com.ore.backend.chatMessage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AiChatRequest {

    private UUID sessionId;
    private String message;
    private AiProductInfo product;
    private String manualContent;
    private String manualUrl;
    private List<AiChatHistory> history;
}