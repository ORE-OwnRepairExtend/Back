package com.ore.backend.repairReminder.repository;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairReminderRepository extends JpaRepository<RepairReminderEntity, UUID> {

    // 특정 사용자의 미완료 수리 알림 목록 조회
    @EntityGraph(attributePaths = {"product", "product.user"})
    List<RepairReminderEntity> findByProduct_User_UserIdAndIsDoneFalse(UUID userId);

    Optional<RepairReminderEntity> findByRepairHistory_RepairIdAndProduct_ProductIdAndProduct_User_UserId(
            UUID repairId,
            UUID productId,
            UUID userId
    );
}