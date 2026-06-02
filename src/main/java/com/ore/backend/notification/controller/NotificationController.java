package com.ore.backend.notification.controller;

import com.ore.backend.notification.dto.GetNotificationResponse;
import com.ore.backend.notification.dto.NotificationUnreadCountResponse;
import com.ore.backend.notification.service.NotificationService;
import com.ore.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @GetMapping
    public List<GetNotificationResponse> getNotifications(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return notificationService.getNotifications(userId);
    }

    @GetMapping("/unread-count")
    public NotificationUnreadCountResponse getUnreadCount(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        return notificationService.getUnreadCount(userId);
    }
}