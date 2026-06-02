package com.ore.backend.notification.service;

import com.ore.backend.notification.entity.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationStatusCalculator {

    public NotificationStatus calculate(long remainingDays) {
        if (remainingDays <= 0) {
            return NotificationStatus.EXPIRED;
        }

        if (remainingDays <= 7) {
            return NotificationStatus.DANGER;
        }

        if (remainingDays <= 30) {
            return NotificationStatus.WARNING;
        }

        return NotificationStatus.NORMAL;
    }
}
