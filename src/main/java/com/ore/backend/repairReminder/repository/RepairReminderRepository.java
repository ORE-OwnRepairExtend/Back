package com.ore.backend.repairReminder.repository;

import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairReminderRepository extends JpaRepository<RepairReminderEntity, UUID> {

    // 특정 사용자의 미완료 수리 알림 목록 조회
    @EntityGraph(attributePaths = {"product", "product.user"})
    List<RepairReminderEntity> findByProduct_User_UserIdAndIsDoneFalse(UUID userId);

    // 사용자의 전체 수리 리마인더 목록 조회
    @EntityGraph(attributePaths = {"product"})
    List<RepairReminderEntity> findByProduct_User_UserIdOrderByRemindAtAscCreatedAtDesc(UUID userId);

    // 특정 제품의 수리 리마인더 목록 조회
    List<RepairReminderEntity> findByProduct_ProductIdOrderByRemindAtAscCreatedAtDesc(UUID productId);

    Optional<RepairReminderEntity> findByReminderIdAndProduct_ProductIdAndProduct_User_UserId(
            UUID reminderId,
            UUID productId,
            UUID userId
    );

    void deleteByProduct_ProductId(UUID productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RepairReminderEntity r SET r.repairHistory = null WHERE r.repairHistory.repairId = :repairId")
    void updateRepairToNullByRepairId(@Param("repairId") UUID repairId);
}