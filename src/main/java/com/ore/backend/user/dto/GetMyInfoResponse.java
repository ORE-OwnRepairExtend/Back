package com.ore.backend.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetMyInfoResponse {

    private UUID userId;
    private String email;
    private String name;
    private String profileImage;
    private LocalDateTime createdAt;
}