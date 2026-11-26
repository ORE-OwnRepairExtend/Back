package com.ore.backend.ownershipTransfer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOwnershipTransferRequest {

    private String recipientEmail;
}
