package com.ore.backend.family.controller;

import com.ore.backend.family.dto.GetFamilyMemberResponse;
import com.ore.backend.family.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/family")
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping("/members")
    public List<GetFamilyMemberResponse> getFamilyMembers(
            @AuthenticationPrincipal UUID userId
    ) {
        return familyService.getFamilyMembers(userId);
    }
}
