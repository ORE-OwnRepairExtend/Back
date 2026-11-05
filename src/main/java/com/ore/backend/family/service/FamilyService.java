package com.ore.backend.family.service;

import com.ore.backend.family.dto.GetFamilyMemberResponse;
import com.ore.backend.family.dto.CreateFamilyInvitationRequest;
import com.ore.backend.family.dto.CreateFamilyInvitationResponse;
import com.ore.backend.family.entity.FamilyInvitationEntity;
import com.ore.backend.family.entity.FamilyInvitationStatus;
import com.ore.backend.family.repository.FamilyInvitationRepository;
import com.ore.backend.family.repository.FamilyMembershipRepository;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.s3.S3ImageStorageService;
import com.ore.backend.notification.service.NotificationCreateService;
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
    private final FamilyInvitationRepository familyInvitationRepository;
    private final NotificationCreateService notificationCreateService;
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

    @Transactional
    public CreateFamilyInvitationResponse createInvitation(
            UUID requesterUserId,
            CreateFamilyInvitationRequest request
    ) {
        validateInvitationRequest(request);

        UserEntity requester = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        UserEntity recipient = userRepository.findByEmail(request.getRecipientEmail().trim())
                .orElseThrow(() -> new CustomException(ErrorCode.INVITEE_NOT_FOUND));

        if (requester.getUserId().equals(recipient.getUserId())) {
            throw new CustomException(ErrorCode.CANNOT_INVITE_SELF);
        }

        validateNoExistingFamilyRelation(requester.getUserId(), recipient.getUserId());
        validateNoPendingInvitation(requester.getUserId(), recipient.getUserId());

        FamilyInvitationEntity invitation = familyInvitationRepository.save(
                FamilyInvitationEntity.builder()
                        .requesterUser(requester)
                        .recipientUser(recipient)
                        .requestedNickname(request.getNickname().trim())
                        .build()
        );

        notificationCreateService.createFamilyInvitationNotification(invitation);

        return CreateFamilyInvitationResponse.from(invitation);
    }

    private void validateInvitationRequest(CreateFamilyInvitationRequest request) {
        if (request == null
                || request.getRecipientEmail() == null || request.getRecipientEmail().isBlank()
                || request.getNickname() == null || request.getNickname().isBlank()) {
            throw new CustomException(ErrorCode.FAMILY_INVITATION_FIELDS_REQUIRED);
        }
    }

    private void validateNoExistingFamilyRelation(UUID requesterUserId, UUID recipientUserId) {
        boolean alreadyFamily = familyMembershipRepository
                .existsByUser_UserIdAndFamilyUser_UserIdAndDeletedAtIsNull(requesterUserId, recipientUserId)
                || familyMembershipRepository
                .existsByUser_UserIdAndFamilyUser_UserIdAndDeletedAtIsNull(recipientUserId, requesterUserId);

        if (alreadyFamily) {
            throw new CustomException(ErrorCode.ALREADY_FAMILY_MEMBER);
        }
    }

    private void validateNoPendingInvitation(UUID requesterUserId, UUID recipientUserId) {
        boolean pendingInvitationExists = familyInvitationRepository
                .existsByRequesterUser_UserIdAndRecipientUser_UserIdAndStatus(
                        requesterUserId,
                        recipientUserId,
                        FamilyInvitationStatus.PENDING
                )
                || familyInvitationRepository
                .existsByRequesterUser_UserIdAndRecipientUser_UserIdAndStatus(
                        recipientUserId,
                        requesterUserId,
                        FamilyInvitationStatus.PENDING
                );

        if (pendingInvitationExists) {
            throw new CustomException(ErrorCode.PENDING_FAMILY_INVITATION_EXISTS);
        }
    }
}
