package com.ore.backend.repairReminder.entity;

import com.ore.backend.product.entity.ProductEntity;
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

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "remind_date", nullable = false)
    private LocalDate remindDate;

    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public RepairReminderEntity(
            ProductEntity product,
            String title,
            String memo,
            LocalDate remindDate
    ) {
        this.product = product;
        this.title = title;
        this.memo = memo;
        this.remindDate = remindDate;
        this.isDone = false;
    }

    public void updateReminderInfo(String title, String memo, LocalDate remindDate) {
        if (title != null) {
            this.title = title;
        }

        if (memo != null) {
            this.memo = memo;
        }

        if (remindDate != null) {
            this.remindDate = remindDate;
        }
    }

    public void complete() {
        this.isDone = true;
        this.completedAt = LocalDateTime.now();
    }

    public void cancelComplete() {
        this.isDone = false;
        this.completedAt = null;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.isDone == null) {
            this.isDone = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}