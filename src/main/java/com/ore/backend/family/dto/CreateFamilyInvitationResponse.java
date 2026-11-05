package com.ore.backend.family.dto;

import com.ore.backend.family.entity.FamilyInvitationEntity;
import com.ore.backend.family.entity.FamilyInvitationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateFamilyInvitationResponse {

    private UUID invitationId;
    private UUID recipientUserId;
    private String recipientName;
    private String nickname;
    private FamilyInvitationStatus status;
    private LocalDateTime createdAt;

    public static CreateFamilyInvitationResponse from(FamilyInvitationEntity invitation) {
        return CreateFamilyInvitationResponse.builder()
                .invitationId(invitation.getFamilyInvitationId())
                .recipientUserId(invitation.getRecipientUser().getUserId())
                .recipientName(invitation.getRecipientUser().getName())
                .nickname(invitation.getRequestedNickname())
                .status(invitation.getStatus())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
