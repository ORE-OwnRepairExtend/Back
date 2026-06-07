package com.ore.backend.chatMessage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AiProductInfo {

    private UUID productId;
    private String name;
    private String nickname;
    private String category;
}