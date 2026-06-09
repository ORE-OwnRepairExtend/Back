package com.ore.backend.chatSession.repository;

import com.ore.backend.chatSession.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {

    List<ChatSessionEntity> findByUser_UserIdOrderByCreatedAtDesc(UUID userId);

    Optional<ChatSessionEntity> findBySessionIdAndUser_UserId(UUID sessionId, UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ChatSessionEntity c SET c.product = null WHERE c.product.productId = :productId")
    void updateProductToNullByProductId(@Param("productId") UUID productId);

}