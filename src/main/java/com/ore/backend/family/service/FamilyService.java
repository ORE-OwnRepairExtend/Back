package com.ore.backend.family.service;

import com.ore.backend.family.dto.GetFamilyMemberResponse;
import com.ore.backend.family.dto.CreateFamilyInvitationRequest;
import com.ore.backend.family.dto.CreateFamilyInvitationResponse;
import com.ore.backend.family.entity.FamilyInvitationEntity;
import com.ore.backend.family.entity.FamilyInvitationStatus;
import com.ore.backend.family.dto.RespondFamilyInvitationRequest;
import com.ore.backend.family.dto.RespondFamilyInvitationResponse;
import com.ore.backend.family.entity.FamilyMembershipEntity;
import com.ore.backend.family.repository.FamilyInvitationRepository;
import com.ore.backend.family.repository.FamilyMembershipRepository;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.global.s3.S3ImageStorageService;
import com.ore.backend.notification.service.NotificationCreateService;
import com.ore.backend.notification.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;
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

    @Transactional
    public RespondFamilyInvitationResponse respondToInvitation(
            UUID recipientUserId,
            UUID invitationId,
            RespondFamilyInvitationRequest request
    ) {
        FamilyInvitationEntity invitation = familyInvitationRepository.findWithLockByFamilyInvitationId(invitationId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAMILY_INVITATION_NOT_FOUND));

        if (!invitation.getRecipientUser().getUserId().equals(recipientUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_FAMILY_INVITATION);
        }

        if (invitation.getStatus() != FamilyInvitationStatus.PENDING) {
            throw new CustomException(ErrorCode.FAMILY_INVITATION_ALREADY_PROCESSED);
        }

        validateResponseStatus(request);
        invitation.respond(request.getStatus());

        if (request.getStatus() == FamilyInvitationStatus.ACCEPTED) {
            createBidirectionalMemberships(invitation);
        }

        markInvitationNotificationAsRead(recipientUserId, invitationId);

        return RespondFamilyInvitationResponse.from(invitation);
    }

    @Transactional
    public void removeFamilyMember(UUID userId, UUID familyUserId) {
        FamilyMembershipEntity membership = familyMembershipRepository
                .findByUser_UserIdAndFamilyUser_UserIdAndDeletedAtIsNull(userId, familyUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAMILY_MEMBER_NOT_FOUND));

        familyMembershipRepository.delete(membership);
        familyMembershipRepository
                .findByUser_UserIdAndFamilyUser_UserIdAndDeletedAtIsNull(familyUserId, userId)
                .ifPresent(familyMembershipRepository::delete);
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
                .existsActiveRelationshipBetween(requesterUserId, recipientUserId);

        if (alreadyFamily) {
            throw new CustomException(ErrorCode.ALREADY_FAMILY_MEMBER);
        }
    }

    private void validateNoPendingInvitation(UUID requesterUserId, UUID recipientUserId) {
        boolean pendingInvitationExists = familyInvitationRepository
                .existsPendingInvitationBetween(
                        requesterUserId,
                        recipientUserId,
                        FamilyInvitationStatus.PENDING
                );

        if (pendingInvitationExists) {
            throw new CustomException(ErrorCode.PENDING_FAMILY_INVITATION_EXISTS);
        }
    }

    private void validateResponseStatus(RespondFamilyInvitationRequest request) {
        if (request == null
                || (request.getStatus() != FamilyInvitationStatus.ACCEPTED
                && request.getStatus() != FamilyInvitationStatus.REJECTED)) {
            throw new CustomException(ErrorCode.INVALID_FAMILY_INVITATION_STATUS);
        }
    }

    private void createBidirectionalMemberships(FamilyInvitationEntity invitation) {
        UserEntity requester = invitation.getRequesterUser();
        UserEntity recipient = invitation.getRecipientUser();

        familyMembershipRepository.saveAll(List.of(
                FamilyMembershipEntity.builder()
                        .user(requester)
                        .familyUser(recipient)
                        .nickname(invitation.getRequestedNickname())
                        .build(),
                FamilyMembershipEntity.builder()
                        .user(recipient)
                        .familyUser(requester)
                        .nickname(requester.getName())
                        .build()
        ));
    }

    private void markInvitationNotificationAsRead(UUID recipientUserId, UUID invitationId) {
        notificationRepository.findByUser_UserIdAndReferenceTypeAndReferenceId(
                        recipientUserId,
                        FamilyInvitationEntity.NOTIFICATION_REFERENCE_TYPE,
                        invitationId
                )
                .ifPresent(notification -> notification.updateRead());
    }
}
