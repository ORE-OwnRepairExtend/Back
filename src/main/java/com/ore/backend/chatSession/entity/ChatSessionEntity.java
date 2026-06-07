package com.ore.backend.chatSession.entity;

import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_sessions")
public class ChatSessionEntity {

    @Id
    @GeneratedValue
    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // productId가 null이면 기타 질문 세션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private ChatSessionEntity(UserEntity user, ProductEntity product) {
        this.user = user;
        this.product = product;
    }

    public static ChatSessionEntity create(UserEntity user, ProductEntity product) {
        return new ChatSessionEntity(user, product);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}