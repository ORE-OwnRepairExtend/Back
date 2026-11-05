package com.ore.backend.family.repository;

import com.ore.backend.family.entity.FamilyInvitationEntity;
import com.ore.backend.family.entity.FamilyInvitationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FamilyInvitationRepository extends JpaRepository<FamilyInvitationEntity, UUID> {

    @Query("""
            select count(i) > 0
            from FamilyInvitationEntity i
            where i.status = :status
              and ((i.requesterUser.userId = :firstUserId and i.recipientUser.userId = :secondUserId)
                or (i.requesterUser.userId = :secondUserId and i.recipientUser.userId = :firstUserId))
            """)
    boolean existsPendingInvitationBetween(
            @Param("firstUserId") UUID firstUserId,
            @Param("secondUserId") UUID secondUserId,
            @Param("status") FamilyInvitationStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select i
            from FamilyInvitationEntity i
            where i.familyInvitationId = :invitationId
            """)
    Optional<FamilyInvitationEntity> findWithLockByFamilyInvitationId(
            @Param("invitationId") UUID invitationId
    );
}
