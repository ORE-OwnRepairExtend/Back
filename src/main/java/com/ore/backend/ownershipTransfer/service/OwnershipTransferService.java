package com.ore.backend.ownershipTransfer.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.notification.service.NotificationCreateService;
import com.ore.backend.ownershipTransfer.dto.CreateOwnershipTransferRequest;
import com.ore.backend.ownershipTransfer.dto.CreateOwnershipTransferResponse;
import com.ore.backend.ownershipTransfer.entity.OwnershipTransferEntity;
import com.ore.backend.ownershipTransfer.entity.OwnershipTransferStatus;
import com.ore.backend.ownershipTransfer.repository.OwnershipTransferRepository;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.user.entity.UserEntity;
import com.ore.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnershipTransferService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OwnershipTransferRepository ownershipTransferRepository;
    private final NotificationCreateService notificationCreateService;

    @Transactional
    public CreateOwnershipTransferResponse createTransferRequest(
            UUID requesterUserId,
            UUID productId,
            CreateOwnershipTransferRequest request
    ) {
        String recipientEmail = validateAndNormalizeRecipientEmail(request);

        ProductEntity product = productRepository.findWithLockByProductId(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getUser().getUserId().equals(requesterUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_OWNERSHIP_TRANSFER_REQUEST);
        }

        UserEntity recipient = userRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.OWNERSHIP_TRANSFER_RECIPIENT_NOT_FOUND));

        if (requesterUserId.equals(recipient.getUserId())) {
            throw new CustomException(ErrorCode.CANNOT_TRANSFER_TO_SELF);
        }

        if (ownershipTransferRepository.existsByProduct_ProductIdAndStatus(
                productId,
                OwnershipTransferStatus.PENDING
        )) {
            throw new CustomException(ErrorCode.PENDING_OWNERSHIP_TRANSFER_EXISTS);
        }

        OwnershipTransferEntity transfer = ownershipTransferRepository.save(
                OwnershipTransferEntity.builder()
                        .product(product)
                        .requesterUser(product.getUser())
                        .recipientUser(recipient)
                        .recipientEmail(recipientEmail)
                        .build()
        );

        notificationCreateService.createOwnershipTransferNotification(transfer);

        return CreateOwnershipTransferResponse.from(transfer);
    }

    private String validateAndNormalizeRecipientEmail(CreateOwnershipTransferRequest request) {
        if (request == null || request.getRecipientEmail() == null || request.getRecipientEmail().isBlank()) {
            throw new CustomException(ErrorCode.OWNERSHIP_TRANSFER_RECIPIENT_REQUIRED);
        }

        return request.getRecipientEmail().trim();
    }
}
