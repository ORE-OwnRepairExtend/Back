package com.ore.backend.repairHistory.repository;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    // 제품의 수리 이력 존재 여부 확인
    boolean existsByProduct_ProductId(UUID productId);

    // 제품 ID 목록 중 수리 이력이 존재하는 제품 ID 목록 조회
    @Query("""
            select distinct rh.product.productId
            from RepairHistoryEntity rh
            where rh.product.productId in :productIds
            """)
    List<UUID> findProductIdsHavingRepairHistory(List<UUID> productIds);
}