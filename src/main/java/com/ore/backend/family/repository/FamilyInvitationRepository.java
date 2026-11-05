package com.ore.backend.family.repository;

import com.ore.backend.family.entity.FamilyInvitationEntity;
import com.ore.backend.family.entity.FamilyInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyInvitationRepository extends JpaRepository<FamilyInvitationEntity, UUID> {

    boolean existsByRequesterUser_UserIdAndRecipientUser_UserIdAndStatus(
            UUID requesterUserId,
            UUID recipientUserId,
            FamilyInvitationStatus status
    );
}
