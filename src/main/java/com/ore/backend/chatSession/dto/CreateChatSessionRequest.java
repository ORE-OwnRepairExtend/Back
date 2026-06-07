package com.ore.backend.chatSession.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateChatSessionRequest {

    private UUID productId;
}