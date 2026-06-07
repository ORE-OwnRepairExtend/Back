package com.ore.backend.chatSession.service;

import com.ore.backend.chatSession.dto.CreateChatSessionRequest;
import com.ore.backend.chatSession.dto.CreateChatSessionResponse;
import com.ore.backend.chatSession.entity.ChatSessionEntity;
import com.ore.backend.chatSession.repository.ChatSessionRepository;
import com.ore.backend.global.exception.CustomException;
import com.ore.backend.global.exception.ErrorCode;
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
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CreateChatSessionResponse createChatSession(
            UUID userId,
            CreateChatSessionRequest request
    ) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 일반 질문 세션 처리를 위한 productId null 허용
        UUID productId = request == null ? null : request.getProductId();

        ProductEntity product = getProductIfExists(userId, productId);

        ChatSessionEntity chatSession = ChatSessionEntity.create(user, product);
        ChatSessionEntity savedChatSession = chatSessionRepository.save(chatSession);

        return CreateChatSessionResponse.from(savedChatSession);
    }

    private ProductEntity getProductIfExists(UUID userId, UUID productId) {
        // 기타 질문 세션의 제품 미연결 처리
        if (productId == null) {
            return null;
        }

        return productRepository.findByProductIdAndUser_UserId(productId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}