package com.ore.backend.ownershipTransfer.controller;

import com.ore.backend.ownershipTransfer.dto.RespondOwnershipTransferRequest;
import com.ore.backend.ownershipTransfer.dto.RespondOwnershipTransferResponse;
import com.ore.backend.ownershipTransfer.service.OwnershipTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ownership-transfers")
public class OwnershipTransferController {

    private final OwnershipTransferService ownershipTransferService;

    @PatchMapping("/{transferId}")
    public RespondOwnershipTransferResponse respondToTransfer(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID transferId,
            @RequestBody RespondOwnershipTransferRequest request
    ) {
        return ownershipTransferService.respondToTransfer(userId, transferId, request);
    }
}
