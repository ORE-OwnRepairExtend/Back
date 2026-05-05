package com.ore.backend.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMyInfoRequest {

    private String name;
    private String profileImage;
}
