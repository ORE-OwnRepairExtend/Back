package com.ore.backend.chatMessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendChatMessageRequest {

    private String message;
}