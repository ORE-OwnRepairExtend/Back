package com.ore.backend.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateMyInfoResponse {

    private UUID userId;
    private String name;
    private String profileImage;
}