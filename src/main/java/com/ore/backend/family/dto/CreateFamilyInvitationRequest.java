package com.ore.backend.family.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateFamilyInvitationRequest {

    private String recipientEmail;
    private String nickname;
}
