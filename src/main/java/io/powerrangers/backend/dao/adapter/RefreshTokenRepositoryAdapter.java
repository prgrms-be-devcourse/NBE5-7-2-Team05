package io.powerrangers.backend.dao.adapter;

import io.powerrangers.backend.dao.RefreshTokenBlackListRepository;
import io.powerrangers.backend.dao.RefreshTokenRepository;
import io.powerrangers.backend.dao.TokenRepository;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.RefreshTokenBlackList;
import io.powerrangers.backend.entity.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements TokenRepository {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenBlackListRepository refreshTokenBlackListRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void save(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(token);
    }

    @Override
    public boolean tokenBlackList(String refreshToken) {
        return refreshTokenBlackListRepository.existsByRefreshToken_RefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public RefreshTokenBlackList addBlackList(RefreshToken refreshToken) {
        RefreshTokenBlackList blackList = RefreshTokenBlackList.builder()
                .refreshToken(refreshToken)
                .build();
        return refreshTokenBlackListRepository.save(blackList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findValidRefreshToken(Long userId) {
        String jpql =
                "SELECT rt FROM RefreshToken rt LEFT JOIN RefreshTokenBlackList rtbl ON rt.user.id = :userId AND rtbl.refreshToken IS NULL";
        return entityManager.createQuery(jpql, RefreshToken.class)
                .setParameter("userId",userId)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional
    public List<RefreshToken> findAllRefreshTokensByUserId(Long userId) {
        String jpql = "SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId";
        return entityManager.createQuery(jpql, RefreshToken.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
