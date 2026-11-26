package com.ore.backend.ownershipTransfer.dto;

import com.ore.backend.ownershipTransfer.entity.OwnershipTransferEntity;
import com.ore.backend.ownershipTransfer.entity.OwnershipTransferStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateOwnershipTransferResponse {

    private UUID transferId;
    private UUID productId;
    private UUID recipientUserId;
    private String recipientName;
    private String recipientEmail;
    private OwnershipTransferStatus status;
    private LocalDateTime requestedAt;

    public static CreateOwnershipTransferResponse from(OwnershipTransferEntity transfer) {
        return CreateOwnershipTransferResponse.builder()
                .transferId(transfer.getTransferId())
                .productId(transfer.getProduct().getProductId())
                .recipientUserId(transfer.getRecipientUser().getUserId())
                .recipientName(transfer.getRecipientUser().getName())
                .recipientEmail(transfer.getRecipientEmail())
                .status(transfer.getStatus())
                .requestedAt(transfer.getRequestedAt())
                .build();
    }
}
