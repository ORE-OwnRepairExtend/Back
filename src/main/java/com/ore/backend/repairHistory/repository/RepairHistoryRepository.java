package com.ore.backend.repairHistory.repository;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairHistoryRepository extends JpaRepository<RepairHistoryEntity, UUID> {

    List<RepairHistoryEntity> findByProduct_ProductIdOrderByDateDescCreatedAtDesc(UUID productId);

    Optional<RepairHistoryEntity> findByRepairIdAndProduct_User_UserId(
            UUID repairId,
            UUID userId
    );

    Optional<RepairHistoryEntity> findByRepairIdAndProduct_ProductIdAndProduct_User_UserId(
            UUID repairId,
            UUID productId,
            UUID userId
    );
}