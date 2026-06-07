package com.ore.backend.chatSession.controller;

import com.ore.backend.chatSession.dto.CreateChatSessionRequest;
import com.ore.backend.chatSession.dto.CreateChatSessionResponse;
import com.ore.backend.chatSession.service.ChatSessionService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/sessions")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public CreateChatSessionResponse createChatSession(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateChatSessionRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return chatSessionService.createChatSession(userId, request);
    }
}