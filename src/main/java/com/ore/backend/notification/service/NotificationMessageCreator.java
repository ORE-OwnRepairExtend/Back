package com.ore.backend.notification.service;

import com.ore.backend.notification.entity.NotificationStatus;
import com.ore.backend.notification.entity.ReminderNotificationStage;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageCreator {

    // 보증기간 알림 메시지 생성
    public String createWarrantyMessage(NotificationStatus status, long remainingDays) {
        if (status == NotificationStatus.EXPIRED) {
            return "해당 제품의 보증기간이 만료되었습니다.";
        }

        return "해당 제품의 보증기간이 " + remainingDays + "일 남았습니다.";
    }

    // 수리/관리 리마인더 알림 메시지 생성
    public String createRepairReminderMessage(ReminderNotificationStage stage) {
        if (stage == ReminderNotificationStage.D_DAY) {
            return "오늘은 등록한 관리/수리 예정일입니다.";
        }

        if (stage == ReminderNotificationStage.AFTER_7_DAYS) {
            return "관리/수리 예정일로부터 1주일이 지났습니다. 아직 완료하지 않았다면 확인해주세요.";
        }

        return "관리/수리 예정일로부터 한 달이 지났습니다. 아직 완료하지 않았다면 확인해주세요.";
    }
}
