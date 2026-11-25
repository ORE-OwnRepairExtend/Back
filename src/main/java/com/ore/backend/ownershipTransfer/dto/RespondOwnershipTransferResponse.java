package com.ore.backend.ownershipTransfer.dto;

import com.ore.backend.ownershipTransfer.entity.OwnershipTransferEntity;
import com.ore.backend.ownershipTransfer.entity.OwnershipTransferStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RespondOwnershipTransferResponse {

    private UUID transferId;
    private UUID productId;
    private OwnershipTransferStatus status;
    private User fromUser;
    private User toUser;
    private LocalDateTime respondedAt;
    private LocalDateTime transferredAt;

    public static RespondOwnershipTransferResponse from(OwnershipTransferEntity transfer) {
        return RespondOwnershipTransferResponse.builder()
                .transferId(transfer.getTransferId())
                .productId(transfer.getProduct().getProductId())
                .status(transfer.getStatus())
                .fromUser(User.from(transfer.getRequesterUser().getUserId(), transfer.getRequesterUser().getName()))
                .toUser(User.from(transfer.getRecipientUser().getUserId(), transfer.getRecipientUser().getName()))
                .respondedAt(transfer.getRespondedAt())
                .transferredAt(transfer.getTransferredAt())
                .build();
    }

    @Getter
    @Builder
    public static class User {
        private UUID userId;
        private String name;

        private static User from(UUID userId, String name) {
            return User.builder()
                    .userId(userId)
                    .name(name)
                    .build();
        }
    }
}
