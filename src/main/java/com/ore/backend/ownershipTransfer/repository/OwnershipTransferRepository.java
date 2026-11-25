package com.ore.backend.ownershipTransfer.repository;

import com.ore.backend.ownershipTransfer.entity.OwnershipTransferEntity;
import com.ore.backend.ownershipTransfer.entity.OwnershipTransferStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OwnershipTransferRepository extends JpaRepository<OwnershipTransferEntity, UUID> {

    boolean existsByProduct_ProductIdAndStatus(UUID productId, OwnershipTransferStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from OwnershipTransferEntity t
            join fetch t.product
            join fetch t.requesterUser
            join fetch t.recipientUser
            where t.transferId = :transferId
            """)
    Optional<OwnershipTransferEntity> findWithLockByTransferId(@Param("transferId") UUID transferId);
}
