package com.ore.backend.product.service;

import com.ore.backend.family.repository.FamilyMembershipRepository;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAccessService {

    private final FamilyMembershipRepository familyMembershipRepository;

    public void validateReadAccess(UUID userId, ProductEntity product) {
        boolean isOwner = product.getUser().getUserId().equals(userId);
        boolean isFamilyMember = !isOwner && familyMembershipRepository.existsActiveRelationshipBetween(
                userId,
                product.getUser().getUserId()
        );

        if (!isOwner && !isFamilyMember) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }
}
