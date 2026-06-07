package com.ore.backend.chatMessage.controller;

import com.ore.backend.chatMessage.dto.ChatMessageResponse;
import com.ore.backend.chatMessage.dto.SendChatMessageRequest;
import com.ore.backend.chatMessage.dto.SendChatMessageResponse;
import com.ore.backend.chatMessage.service.ChatMessageService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/sessions/{sessionId}/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public SendChatMessageResponse sendMessage(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID sessionId,
            @RequestBody SendChatMessageRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return chatMessageService.sendMessage(userId, sessionId, request);
    }

    @GetMapping
    public List<ChatMessageResponse> getMessages(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID sessionId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return chatMessageService.getMessages(userId, sessionId);
    }
}