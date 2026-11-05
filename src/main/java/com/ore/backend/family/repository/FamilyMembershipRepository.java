package com.ore.backend.family.repository;

import com.ore.backend.family.entity.FamilyMembershipEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamilyMembershipRepository extends JpaRepository<FamilyMembershipEntity, UUID> {

    @EntityGraph(attributePaths = "familyUser")
    List<FamilyMembershipEntity> findByUser_UserIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID userId);

    Optional<FamilyMembershipEntity> findByUser_UserIdAndFamilyUser_UserIdAndDeletedAtIsNull(
            UUID userId,
            UUID familyUserId
    );

    @Query("""
            select count(m) > 0
            from FamilyMembershipEntity m
            where m.deletedAt is null
              and ((m.user.userId = :firstUserId and m.familyUser.userId = :secondUserId)
                or (m.user.userId = :secondUserId and m.familyUser.userId = :firstUserId))
            """)
    boolean existsActiveRelationshipBetween(
            @Param("firstUserId") UUID firstUserId,
            @Param("secondUserId") UUID secondUserId
    );
}
