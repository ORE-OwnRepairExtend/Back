package com.ore.backend.user.repository;

import com.ore.backend.user.entity.RefreshTokenEntity;
import com.ore.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByUser(UserEntity user);

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
