package com.ore.backend.family.dto;

import com.ore.backend.family.entity.FamilyInvitationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RespondFamilyInvitationRequest {

    private FamilyInvitationStatus status;

    private String nickname;
}
