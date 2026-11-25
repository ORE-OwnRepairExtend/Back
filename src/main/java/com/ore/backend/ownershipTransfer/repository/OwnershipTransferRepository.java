package com.ore.backend.ownershipTransfer.repository;

import com.ore.backend.ownershipTransfer.entity.OwnershipTransferEntity;
import com.ore.backend.ownershipTransfer.entity.OwnershipTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OwnershipTransferRepository extends JpaRepository<OwnershipTransferEntity, UUID> {

    boolean existsByProduct_ProductIdAndStatus(UUID productId, OwnershipTransferStatus status);
}
