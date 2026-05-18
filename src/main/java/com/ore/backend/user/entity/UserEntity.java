package com.ore.backend.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue
    @JdbcType(BinaryJdbcType.class)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    // 구글에서 내려주는 고유 ID
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    // S3 object key 저장
    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 생성 시 자동 세팅
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String profileImage) {
        if (name != null) {
            this.name = name;
        }

        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }
}