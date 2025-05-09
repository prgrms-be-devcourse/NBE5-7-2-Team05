package io.powerrangers.backend.config;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.TokenPair;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.service.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 추후 userService로 바꾸면 좋을 듯
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String BASE_URL = "http://localhost:8080/user";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User findUser = userRepository.findById(principal.getId()).orElseThrow(
                () -> new RuntimeException("존재하지 않는 유저입니다."));

        HashMap<String, String> paramsMap = new HashMap<>();
        Optional<RefreshToken> validRefreshToken = jwtProvider.findValidRefreshToken(principal.getId());
        if (validRefreshToken.isEmpty()) {
            TokenPair tokenPair = jwtProvider.generateTokenPair(findUser);
            paramsMap.put(ACCESS_TOKEN, tokenPair.getAccessToken());
            paramsMap.put(REFRESH_TOKEN, tokenPair.getRefreshToken());
        } else {
            String accessToken = jwtProvider.issueAccessToken(findUser.getId(), findUser.getRole());
            paramsMap.put(ACCESS_TOKEN, accessToken);
            paramsMap.put(REFRESH_TOKEN, validRefreshToken.get().getRefreshToken());
        }
        String url = generateUrlString(paramsMap);

        // 토큰 값을 출력하기 위한 로그 출력
        log.info("paramsMap.get(ACCESS_TOKEN) = {}", paramsMap.get(ACCESS_TOKEN));
        log.info("paramsMap.get(REFRESH_TOKEN) = {}", paramsMap.get(REFRESH_TOKEN));
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String generateUrlString(Map<String, String> paramsMap) {
        return UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam(ACCESS_TOKEN, paramsMap.get(ACCESS_TOKEN))
                .queryParam(REFRESH_TOKEN, paramsMap.get(REFRESH_TOKEN))
                .build()
                .toUri()
                .toString();
    }
}
