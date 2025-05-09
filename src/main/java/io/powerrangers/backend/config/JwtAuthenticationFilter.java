package io.powerrangers.backend.config;

import io.powerrangers.backend.dto.TokenBody;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.entity.User;
import io.powerrangers.backend.service.CustomOauth2UserService;
import io.powerrangers.backend.service.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomOauth2UserService customOauth2UserService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final List<String> WHITE_LIST = List.of(
            "/test/**"
    );
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null && jwtProvider.validateToken(token)) {
            TokenBody tokenBody = jwtProvider.parseToken(token);
            UserDetails userDetails = customOauth2UserService.getUserDetails(tokenBody.getUserId());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.error("filter 에서 문제 발생!");
            throw new RuntimeException("문제가 있는 토큰입니다.");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return WHITE_LIST.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
