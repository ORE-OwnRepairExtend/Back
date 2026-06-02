package com.ore.backend.notification.service;

import com.ore.backend.notification.entity.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageCreator {

    public String createWarrantyMessage(NotificationStatus status, long remainingDays) {
        if (status == NotificationStatus.EXPIRED) {
            return "해당 제품의 보증기간이 만료되었습니다.";
        }

        return "해당 제품의 보증기간이 " + remainingDays + "일 남았습니다.";
    }

    public String createRepairMessage(NotificationStatus status, long remainingDays) {
        if (status == NotificationStatus.EXPIRED) {
            return "등록한 수리 일정일입니다.";
        }

        return "등록한 수리 일정이 " + remainingDays + "일 남았습니다.";
    }
}
