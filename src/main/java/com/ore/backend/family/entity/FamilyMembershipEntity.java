package com.ore.backend.family.entity;

import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "family_memberships")
public class FamilyMembershipEntity {

    @Id
    @GeneratedValue
    @Column(name = "family_membership_id", nullable = false, updatable = false)
    private UUID familyMembershipId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_user_id", nullable = false)
    private UserEntity familyUser;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public FamilyMembershipEntity(UserEntity user, UserEntity familyUser, String nickname) {
        this.user = user;
        this.familyUser = familyUser;
        this.nickname = nickname;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
