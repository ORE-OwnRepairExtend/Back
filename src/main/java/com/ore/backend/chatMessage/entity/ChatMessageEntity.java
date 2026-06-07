package com.ore.backend.chatMessage.entity;

import com.ore.backend.chatSession.entity.ChatSessionEntity;
import com.ore.backend.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    @GeneratedValue
    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSessionEntity session;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ChatMessageRole role;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private ChatMessageEntity(
            ProductEntity product,
            ChatSessionEntity session,
            ChatMessageRole role,
            String content
    ) {
        this.product = product;
        this.session = session;
        this.role = role;
        this.content = content;
    }

    public static ChatMessageEntity create(
            ProductEntity product,
            ChatSessionEntity session,
            ChatMessageRole role,
            String content
    ) {
        return new ChatMessageEntity(product, session, role, content);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}