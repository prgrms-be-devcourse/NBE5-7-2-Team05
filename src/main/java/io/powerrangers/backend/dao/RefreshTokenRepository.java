package io.powerrangers.backend.dao;

import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<User> findUserByRefreshToken(String refreshTokenValue);
}
