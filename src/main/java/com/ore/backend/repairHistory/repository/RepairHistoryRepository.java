package com.ore.backend.repairHistory.repository;

import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepairHistoryRepository extends JpaRepository<RepairHistoryEntity, UUID> {
}