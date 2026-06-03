package com.ore.backend.notification.service;

import com.ore.backend.notification.entity.ReminderNotificationStage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ReminderNotificationStageCalculator {

    // remindAt 기준으로 생성해야 할 리마인더 알림 단계를 계산
    public Optional<ReminderNotificationStage> calculate(LocalDate remindAt) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(remindAt)) {
            return Optional.empty();
        }

        if (!today.isBefore(remindAt.plusMonths(1))) {
            return Optional.of(ReminderNotificationStage.AFTER_1_MONTH);
        }

        if (!today.isBefore(remindAt.plusDays(7))) {
            return Optional.of(ReminderNotificationStage.AFTER_7_DAYS);
        }

        return Optional.of(ReminderNotificationStage.D_DAY);
    }
}