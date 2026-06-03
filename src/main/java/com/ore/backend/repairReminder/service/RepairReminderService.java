package com.ore.backend.repairReminder.service;

import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.product.repository.ProductRepository;
import com.ore.backend.repairHistory.entity.RepairHistoryEntity;
import com.ore.backend.repairHistory.repository.RepairHistoryRepository;
import com.ore.backend.repairReminder.dto.CreateRepairReminderRequest;
import com.ore.backend.repairReminder.dto.CreateRepairReminderResponse;
import com.ore.backend.repairReminder.dto.GetProductRepairReminderResponse;
import com.ore.backend.repairReminder.dto.GetRepairReminderResponse;
import com.ore.backend.repairReminder.entity.RepairReminderEntity;
import com.ore.backend.repairReminder.repository.RepairReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepairReminderService {

    private final ProductRepository productRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final RepairReminderRepository repairReminderRepository;

    // 수리/관리 예정 알림 등록
    @Transactional
    public CreateRepairReminderResponse createRepairReminder(
            UUID userId,
            UUID productId,
            CreateRepairReminderRequest request
    ) {
        validateRequiredFields(request);

        ProductEntity product = productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        RepairHistoryEntity repairHistory = findRepairHistoryIfExists(
                userId,
                productId,
                request.getRepairId()
        );

        RepairReminderEntity reminder = RepairReminderEntity.builder()
                .product(product)
                .repairHistory(repairHistory)
                .title(request.getTitle())
                .remindAt(request.getRemindAt())
                .build();

        RepairReminderEntity savedReminder = repairReminderRepository.save(reminder);

        return CreateRepairReminderResponse.from(savedReminder);
    }

    private void validateRequiredFields(CreateRepairReminderRequest request) {
        if (request == null
                || request.getTitle() == null || request.getTitle().isBlank()
                || request.getRemindAt() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (!request.getRemindAt().isAfter(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_REMIND_DATE);
        }
    }

    private RepairHistoryEntity findRepairHistoryIfExists(
            UUID userId,
            UUID productId,
            UUID repairId
    ) {
        if (repairId == null) {
            return null;
        }

        return repairHistoryRepository
                .findByRepairIdAndProduct_ProductIdAndProduct_User_UserId(
                        repairId,
                        productId,
                        userId
                )
                .orElseThrow(() -> new CustomException(ErrorCode.REPAIR_HISTORY_NOT_FOUND));
    }

    // 사용자의 전체 수리/관리 예정 목록 조회
    public List<GetRepairReminderResponse> getRepairReminders(UUID userId) {
        return repairReminderRepository
                .findByProduct_User_UserIdOrderByRemindAtAscCreatedAtDesc(userId)
                .stream()
                .map(GetRepairReminderResponse::from)
                .toList();
    }

    // 특정 제품의 수리/관리 예정 목록 조회
    public List<GetProductRepairReminderResponse> getProductRepairReminders(
            UUID userId,
            UUID productId
    ) {
        productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return repairReminderRepository
                .findByProduct_ProductIdOrderByRemindAtAscCreatedAtDesc(productId)
                .stream()
                .map(GetProductRepairReminderResponse::from)
                .toList();
    }
}