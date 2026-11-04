package com.ore.backend.family.dto;

import com.ore.backend.family.entity.FamilyMembershipEntity;
import com.ore.backend.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetFamilyMemberResponse {

    private UUID userId;
    private String name;
    private String email;
    private String profileImageUrl;
    private String nickname;
    private boolean isMe;

    public static GetFamilyMemberResponse of(
            UserEntity user,
            String profileImageUrl,
            String nickname,
            boolean isMe
    ) {
        return GetFamilyMemberResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(profileImageUrl)
                .nickname(nickname)
                .isMe(isMe)
                .build();
    }

    public static GetFamilyMemberResponse from(
            FamilyMembershipEntity membership,
            String profileImageUrl
    ) {
        return of(
                membership.getFamilyUser(),
                profileImageUrl,
                membership.getNickname(),
                false
        );
    }
}
