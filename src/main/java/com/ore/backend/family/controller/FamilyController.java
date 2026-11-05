package com.ore.backend.family.controller;

import com.ore.backend.family.dto.GetFamilyMemberResponse;
import com.ore.backend.family.dto.CreateFamilyInvitationRequest;
import com.ore.backend.family.dto.CreateFamilyInvitationResponse;
import com.ore.backend.family.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/invitations")
    public CreateFamilyInvitationResponse createInvitation(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateFamilyInvitationRequest request
    ) {
        return familyService.createInvitation(userId, request);
    }
}
