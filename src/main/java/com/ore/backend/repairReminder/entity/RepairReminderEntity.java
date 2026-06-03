package com.ore.backend.repairReminder.entity;

import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "repair_reminders")
public class RepairReminderEntity {

    @Id
    @GeneratedValue
    @Column(name = "reminder_id", nullable = false, updatable = false)
    private UUID reminderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    private RepairHistoryEntity repairHistory;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "remind_at", nullable = false)
    private LocalDate remindAt;

    @Column(name = "notification_stage", nullable = false)
    private Integer notificationStage = 0;

    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public RepairReminderEntity(
            ProductEntity product,
            RepairHistoryEntity repairHistory,
            String title,
            LocalDate remindAt
    ) {
        this.product = product;
        this.repairHistory = repairHistory;
        this.title = title;
        this.remindAt = remindAt;
        this.notificationStage = 0;
        this.isDone = false;
    }

    public void updateReminderInfo(String title, LocalDate remindAt) {
        if (title != null) {
            this.title = title;
        }

        if (remindAt != null) {
            this.remindAt = remindAt;
        }
    }

    public void complete(LocalDate completedDate) {
        this.isDone = true;
        this.completedDate = completedDate;
    }

    public void cancelComplete() {
        this.isDone = false;
        this.completedDate = null;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.isDone == null) {
            this.isDone = false;
        }

        if (this.notificationStage == null) {
            this.notificationStage = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}