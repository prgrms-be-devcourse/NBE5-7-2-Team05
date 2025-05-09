package io.powerrangers.backend.dao;

import io.powerrangers.backend.entity.RefreshTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenBlackListRepository extends JpaRepository<RefreshTokenBlackList, Long> {
    boolean existsByRefreshToken_RefreshToken(String refreshToken);
}
