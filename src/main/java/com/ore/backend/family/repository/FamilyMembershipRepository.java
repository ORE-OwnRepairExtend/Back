package com.ore.backend.family.repository;

import com.ore.backend.family.entity.FamilyMembershipEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyMembershipRepository extends JpaRepository<FamilyMembershipEntity, UUID> {

    @EntityGraph(attributePaths = "familyUser")
    List<FamilyMembershipEntity> findByUser_UserIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID userId);

    boolean existsByUser_UserIdAndFamilyUser_UserIdAndDeletedAtIsNull(
            UUID userId,
            UUID familyUserId
    );
}
