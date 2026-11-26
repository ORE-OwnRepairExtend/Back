package com.ore.backend.ownershipTransfer.dto;

import com.ore.backend.ownershipTransfer.entity.OwnershipTransferStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RespondOwnershipTransferRequest {

    private OwnershipTransferStatus status;
    private Boolean privacyAgreed;
}
