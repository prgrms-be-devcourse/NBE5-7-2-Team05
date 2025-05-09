package io.powerrangers.backend.dao;

import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.RefreshTokenBlackList;
import io.powerrangers.backend.entity.User;
import java.util.Optional;

public interface TokenRepository {
    void save(User user, String refreshToken);
    boolean tokenBlackList(String refreshToken);
    RefreshTokenBlackList addBlackList(RefreshToken refreshToken);
    Optional<RefreshToken> findValidRefreshToken(Long userId);
}
