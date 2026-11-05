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
@Table(name = "family_invitations")
public class FamilyInvitationEntity {

    public static final String NOTIFICATION_REFERENCE_TYPE = "FAMILY_INVITATION";

    @Id
    @GeneratedValue
    @Column(name = "family_invitation_id", nullable = false, updatable = false)
    private UUID familyInvitationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private UserEntity requesterUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private UserEntity recipientUser;

    @Column(name = "requested_nickname", length = 50)
    private String requestedNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FamilyInvitationStatus status;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public FamilyInvitationEntity(
            UserEntity requesterUser,
            UserEntity recipientUser,
            String requestedNickname
    ) {
        this.requesterUser = requesterUser;
        this.recipientUser = recipientUser;
        this.requestedNickname = requestedNickname;
        this.status = FamilyInvitationStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = FamilyInvitationStatus.PENDING;
        }
    }

    public void respond(FamilyInvitationStatus status) {
        this.status = status;
        this.respondedAt = LocalDateTime.now();
    }
}
