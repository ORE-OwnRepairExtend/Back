package com.ore.backend.family.dto;

import com.ore.backend.family.entity.FamilyInvitationEntity;
import com.ore.backend.family.entity.FamilyInvitationStatus;
import com.ore.backend.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RespondFamilyInvitationResponse {

    private UUID invitationId;
    private FamilyInvitationStatus status;
    private FamilyMember familyMember;
    private LocalDateTime respondedAt;

    public static RespondFamilyInvitationResponse from(
            FamilyInvitationEntity invitation,
            String nickname
    ) {
        return RespondFamilyInvitationResponse.builder()
                .invitationId(invitation.getFamilyInvitationId())
                .status(invitation.getStatus())
                .familyMember(FamilyMember.from(invitation.getRequesterUser(), nickname))
                .respondedAt(invitation.getRespondedAt())
                .build();
    }

    @Getter
    @Builder
    public static class FamilyMember {

        private UUID userId;
        private String name;
        private String email;
        private String nickname;

        private static FamilyMember from(UserEntity user, String nickname) {
            return FamilyMember.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .nickname(nickname)
                    .build();
        }
    }
}
