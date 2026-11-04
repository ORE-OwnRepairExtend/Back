package com.ore.backend.family.service;

import com.ore.backend.family.dto.GetFamilyMemberResponse;
import com.ore.backend.family.repository.FamilyMembershipRepository;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.s3.S3ImageStorageService;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyService {

    private static final String MY_NICKNAME = "나";

    private final UserRepository userRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final S3ImageStorageService s3ImageStorageService;

    public List<GetFamilyMemberResponse> getFamilyMembers(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        GetFamilyMemberResponse me = GetFamilyMemberResponse.of(
                user,
                s3ImageStorageService.generatePresignedUrl(user.getProfileImage()),
                MY_NICKNAME,
                true
        );

        List<GetFamilyMemberResponse> familyMembers = familyMembershipRepository
                .findByUser_UserIdAndDeletedAtIsNullOrderByCreatedAtAsc(userId)
                .stream()
                .map(membership -> GetFamilyMemberResponse.from(
                        membership,
                        s3ImageStorageService.generatePresignedUrl(
                                membership.getFamilyUser().getProfileImage()
                        )
                ))
                .toList();

        return Stream.concat(Stream.of(me), familyMembers.stream())
                .toList();
    }
}
