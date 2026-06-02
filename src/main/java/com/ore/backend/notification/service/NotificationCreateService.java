package com.ore.backend.notification.service;

import com.ore.backend.notification.entity.NotificationEntity;
import com.ore.backend.notification.entity.NotificationStatus;
import com.ore.backend.notification.entity.NotificationType;
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

    @Transactional
    public void createWarrantyNotificationIfNeeded(ProductEntity product, long remainingDays) {
        NotificationStatus status = statusCalculator.calculate(remainingDays);

        if (status == NotificationStatus.NORMAL) {
            return;
        }

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

    @Transactional
    public void createRepairNotificationIfNeeded(RepairReminderEntity reminder, long remainingDays) {
        NotificationStatus status = statusCalculator.calculate(remainingDays);

        if (status == NotificationStatus.NORMAL) {
            return;
        }

        boolean alreadyExists = notificationRepository
                .existsByReminder_ReminderIdAndNotificationTypeAndStatus(
                        reminder.getReminderId(),
                        NotificationType.REPAIR,
                        status
                );

        if (alreadyExists) {
            return;
        }

        String message = messageCreator.createRepairMessage(status, remainingDays);

        NotificationEntity notification = NotificationEntity.builder()
                .user(reminder.getProduct().getUser())
                .product(reminder.getProduct())
                .reminder(reminder)
                .notificationType(NotificationType.REPAIR)
                .status(status)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }
}