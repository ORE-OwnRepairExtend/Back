package com.ore.backend.chatSession.repository;

import com.ore.backend.chatSession.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {
}