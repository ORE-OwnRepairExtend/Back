package com.ore.backend.chatMessage.repository;

import com.ore.backend.chatMessage.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {

    List<ChatMessageEntity> findAllBySession_SessionIdOrderByCreatedAtAsc(UUID sessionId);

    List<ChatMessageEntity> findTop10BySession_SessionIdOrderByCreatedAtDesc(UUID sessionId);

    Optional<ChatMessageEntity> findTopBySession_SessionIdOrderByCreatedAtDesc(UUID sessionId);
}