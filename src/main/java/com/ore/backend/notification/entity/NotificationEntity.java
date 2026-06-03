package com.ore.backend.notification.entity;

import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue
    @Column(name = "notification_id", nullable = false, updatable = false)
    private UUID notificationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // 보증 알림이면 null, 수리 알림이면 값 존재
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id")
    private RepairReminderEntity reminder;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    // 보증 알림 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private NotificationStatus status;

    // 수리/관리 리마인더 알림 단계
    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_stage", length = 50)
    private ReminderNotificationStage reminderStage;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public NotificationEntity(
            UserEntity user,
            ProductEntity product,
            RepairReminderEntity reminder,
            NotificationType notificationType,
            NotificationStatus status,
            ReminderNotificationStage reminderStage,
            String message
    ) {
        this.user = user;
        this.product = product;
        this.reminder = reminder;
        this.notificationType = notificationType;
        this.status = status;
        this.reminderStage = reminderStage;
        this.message = message;
        this.isRead = false;
    }

    public void updateRead() {
        this.isRead = true;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.isRead == null) {
            this.isRead = false;
        }
    }
}