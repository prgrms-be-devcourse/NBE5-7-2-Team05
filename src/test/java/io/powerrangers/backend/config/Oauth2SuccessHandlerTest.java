package io.powerrangers.backend.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.powerrangers.backend.dao.UserRepository;
import io.powerrangers.backend.dto.TokenPair;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.entity.RefreshToken;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.service.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;

@ExtendWith(MockitoExtension.class)
class Oauth2SuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private RefreshToken refreshToken;

    @InjectMocks
    private Oauth2SuccessHandler oauth2SuccessHandler;

    @Test
    void onAuthenticationSuccess_WithoutExistingRefreshToken_ShouldGenerateNewTokensAndRedirect() throws Exception {
        // given
        Long userId = 1L;
        TokenPair tokenPair = new TokenPair("access-token", "refresh-token");
        User user = User.builder().build();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtProvider.findValidRefreshToken(userId)).thenReturn(Optional.empty());
        when(jwtProvider.generateTokenPair(user)).thenReturn(tokenPair);

        // redirect 전략을 별도로 세팅
        RedirectStrategy mockRedirectStrategy = mock(RedirectStrategy.class);
        oauth2SuccessHandler.setRedirectStrategy(mockRedirectStrategy);

        // when
        oauth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(mockRedirectStrategy).sendRedirect(
                eq(request),
                eq(response),
                argThat(url -> url.contains("accessToken=access-token") && url.contains("refreshToken=refresh-token"))
        );
    }
}
