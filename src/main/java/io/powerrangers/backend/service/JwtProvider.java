package io.powerrangers.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import io.powerrangers.backend.dao.TokenRepository;
import io.powerrangers.backend.dto.Role;
import io.powerrangers.backend.dto.TokenBody;
import io.powerrangers.backend.dto.TokenPair;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.User;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {
    private final TokenRepository tokenRepository;

    @Value("${custom.jwt.validation.access}")
    private Long accessTokenExpiration;

    @Value("${custom.jwt.validation.refresh}")
    private Long refreshTokenExpiration;

    @Value("${custom.jwt.secrets.app-key}")
    private String jwtSecret;

    public String issueAccessToken(Long id, Role role) {
        return issueToken(id, role, accessTokenExpiration);
    }

    public String issueRefreshToken(Long id, Role role) {
        return issueToken(id, role, refreshTokenExpiration);
    }

    public TokenPair generateTokenPair(User user) {
        Long id = user.getId();
        Role role = user.getRole();
        String accessToken = issueAccessToken(id,role);
        String refreshToken = issueRefreshToken(id,role);

        tokenRepository.save(user, refreshToken);

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Optional<RefreshToken> findValidRefreshToken(Long userId) {
        return tokenRepository.findValidRefreshToken(userId);
    }

    public boolean validateToken(String token) {
        if(tokenRepository.isTokenBlackList(token)){
            return false;
        }

        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build();
            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("유효하지 않은 토큰입니다");
        } catch (IllegalStateException e) {
            log.error("이상한 토큰입니다");
        } catch (Exception e) {
            log.error("완전히 이상한 토큰입니다.");
        }
        return false;
    }

    public TokenBody parseToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(getSecretKey())
                .build();
        Jws<Claims> claimsJws = parser.parseSignedClaims(token);

        String sub = claimsJws.getPayload().getSubject();
        String role = (String) claimsJws.getPayload().get("role");

        return TokenBody.builder()
                .userId(Long.valueOf(sub))
                .role(role)
                .build();
    }

    private String issueToken(Long id, Role role, Long expiration) {
        return Jwts.builder()
                .subject(id.toString())
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration))
                .signWith(getSecretKey(), SIG.HS256)
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
