package com.ore.backend.notification.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.notification.dto.GetNotificationResponse;
import com.ore.backend.notification.dto.NotificationUnreadCountResponse;
import com.ore.backend.notification.dto.UpdateAllNotificationsReadResponse;
import com.ore.backend.notification.dto.UpdateNotificationReadResponse;
import com.ore.backend.notification.entity.NotificationEntity;
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

    // 알림 목록 조회
    @Transactional
    public List<GetNotificationResponse> getNotifications(UUID userId) {
        notificationGenerateService.generateNotifications(userId);

        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(GetNotificationResponse::from)
                .toList();
    }

    // 안 읽은 알림 개수 조회
    @Transactional
    public NotificationUnreadCountResponse getUnreadCount(UUID userId) {
        notificationGenerateService.generateNotifications(userId);

        long unreadCount = notificationRepository.countByUser_UserIdAndIsReadFalse(userId);

        return NotificationUnreadCountResponse.from(unreadCount);
    }

    // 개별 알림 읽음 처리
    @Transactional
    public UpdateNotificationReadResponse updateRead(
            UUID userId,
            UUID notificationId
    ) {
        NotificationEntity notification = notificationRepository
                .findByNotificationIdAndUser_UserId(notificationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.updateRead();

        return UpdateNotificationReadResponse.from(notification);
    }

    // 전체 알림 읽음 처리
    @Transactional
    public UpdateAllNotificationsReadResponse updateAllRead(UUID userId) {
        // 안 읽은 알림 조회
        List<NotificationEntity> unreadNotifications =
                notificationRepository.findByUser_UserIdAndIsReadFalse(userId);

        unreadNotifications.forEach(NotificationEntity::updateRead);

        return UpdateAllNotificationsReadResponse.from(unreadNotifications.size());
    }
}