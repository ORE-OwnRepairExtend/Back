package com.ore.backend.notification.service;

import com.ore.backend.notification.entity.NotificationEntity;
import com.ore.backend.notification.entity.NotificationStatus;
import com.ore.backend.notification.entity.NotificationType;
import com.ore.backend.notification.entity.ReminderNotificationStage;
import com.ore.backend.notification.repository.NotificationRepository;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCreateService {

    private final NotificationRepository notificationRepository;
    private final NotificationStatusCalculator statusCalculator;
    private final NotificationMessageCreator messageCreator;

    // 보증기간 알림 생성
    @Transactional
    public void createWarrantyNotificationIfNeeded(ProductEntity product, long remainingDays) {
        NotificationStatus status = statusCalculator.calculate(remainingDays);

        if (status == NotificationStatus.NORMAL) {
            return;
        }

        // 동일 제품의 동일 보증 상태 알림 중복 생성 방지
        boolean alreadyExists = notificationRepository
                .existsByProduct_ProductIdAndNotificationTypeAndStatus(
                        product.getProductId(),
                        NotificationType.WARRANTY,
                        status
                );

        if (alreadyExists) {
            return;
        }

        String message = messageCreator.createWarrantyMessage(status, remainingDays);

        NotificationEntity notification = NotificationEntity.builder()
                .user(product.getUser())
                .product(product)
                .reminder(null)
                .notificationType(NotificationType.WARRANTY)
                .status(status)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    // 수리/관리 리마인더 알림 생성
    @Transactional
    public void createRepairReminderNotificationIfNeeded(
            RepairReminderEntity reminder,
            ReminderNotificationStage stage
    ) {
        // 동일 리마인더의 동일 단계 알림 중복 생성 방지
        boolean alreadyExists = notificationRepository
                .existsByReminder_ReminderIdAndNotificationTypeAndReminderStage(
                        reminder.getReminderId(),
                        NotificationType.REPAIR,
                        stage
                );

        if (alreadyExists) {
            return;
        }

        String message = messageCreator.createRepairReminderMessage(stage);

        NotificationEntity notification = NotificationEntity.builder()
                .user(reminder.getProduct().getUser())
                .product(reminder.getProduct())
                .reminder(reminder)
                .notificationType(NotificationType.REPAIR)
                .status(null)
                .reminderStage(stage)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }
}