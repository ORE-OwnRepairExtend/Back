package com.ore.backend.notification.service;

import com.ore.backend.product.dto.ProductWarrantyInfo;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.product.service.ProductWarrantyCalculator;
import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import com.ore.backend.repairReminder.repository.RepairReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationGenerateService {

    private final ProductRepository productRepository;
    private final RepairReminderRepository repairReminderRepository;
    private final ProductWarrantyCalculator productWarrantyCalculator;
    private final NotificationCreateService notificationCreateService;

    public void generateNotifications(UUID userId) {
        generateWarrantyNotifications(userId);
        generateRepairNotifications(userId);
    }

    private void generateWarrantyNotifications(UUID userId) {
        List<ProductEntity> products = productRepository.findByUser_UserId(userId);

        for (ProductEntity product : products) {
            if (!productWarrantyCalculator.canCalculate(product)) {
                continue;
            }

            ProductWarrantyInfo warrantyInfo = productWarrantyCalculator.calculate(product);

            notificationCreateService.createWarrantyNotificationIfNeeded(
                    product,
                    warrantyInfo.getRemainingDays()
            );
        }
    }

    private void generateRepairNotifications(UUID userId) {
        List<RepairReminderEntity> reminders =
                repairReminderRepository.findByProduct_User_UserIdAndIsDoneFalse(userId);

        for (RepairReminderEntity reminder : reminders) {
            long remainingDays = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    reminder.getRemindDate()
            );

            notificationCreateService.createRepairNotificationIfNeeded(
                    reminder,
                    remainingDays
            );
        }
    }
}