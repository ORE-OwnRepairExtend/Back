package com.ore.backend.notification.service;

import com.ore.backend.notification.dto.GetNotificationResponse;
import com.ore.backend.notification.dto.NotificationUnreadCountResponse;
import com.ore.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationGenerateService notificationGenerateService;

    @Transactional
    public List<GetNotificationResponse> getNotifications(UUID userId) {
        notificationGenerateService.generateNotifications(userId);

        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(GetNotificationResponse::from)
                .toList();
    }

    @Transactional
    public NotificationUnreadCountResponse getUnreadCount(UUID userId) {
        notificationGenerateService.generateNotifications(userId);

        long unreadCount = notificationRepository.countByUser_UserIdAndIsReadFalse(userId);

        return NotificationUnreadCountResponse.from(unreadCount);
    }
}