package io.powerrangers.backend.config;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.TokenPair;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.exception.AuthTokenException;
import io.powerrangers.backend.exception.ErrorCode;
import io.powerrangers.backend.service.CookieFactory;
import io.powerrangers.backend.service.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 추후 userService로 바꾸면 좋을 듯
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${custom.oauth2.redirect-url}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User findUser = userRepository.findById(principal.getId()).orElseThrow(
                () -> new AuthTokenException(ErrorCode.USER_NOT_FOUND));

        String accessToken;
        String refreshToken;

        Optional<RefreshToken> validRefreshToken = jwtProvider.findValidRefreshToken(principal.getId());
        if (validRefreshToken.isEmpty()) {
            TokenPair tokenPair = jwtProvider.generateTokenPair(findUser);
            accessToken = tokenPair.getAccessToken();
            refreshToken = tokenPair.getRefreshToken();
        } else {
            accessToken = jwtProvider.issueAccessToken(findUser.getId(), findUser.getRole());
            refreshToken = validRefreshToken.get().getRefreshToken();
        }

        // ✅ 쿠키 생성 및 추가
        ResponseCookie accessCookie = CookieFactory.createAccessCookie(accessToken);
        ResponseCookie refreshCookie = CookieFactory.createRefreshCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("userId", findUser.getId())
                .build()
                .toString();

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
