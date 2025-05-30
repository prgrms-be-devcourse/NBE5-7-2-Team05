package io.powerrangers.backend.service;

import java.time.Duration;
import org.springframework.http.ResponseCookie;

public class CookieFactory {
    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofHours(2);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(14);
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    private CookieFactory() {
    }

    public static ResponseCookie createAccessCookie(String value) {
        return ResponseCookie.from(ACCESS_TOKEN, value)
                .httpOnly(true) // 프론트에서 js로 쿠키 접근 x
                .path("/") // 모든 경로에서 쿠키 사용 가능
                .sameSite("Lax") // 같은 도메인에서 get/post 는 쿠키 전송이 가능함
                .maxAge(ACCESS_TOKEN_EXPIRATION) // 쿠키 유효시간
                .build();
    }

    public static ResponseCookie createRefreshCookie(String value) {
        return ResponseCookie.from(REFRESH_TOKEN, value)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(REFRESH_TOKEN_EXPIRATION)
                .build();
    }

    public static ResponseCookie deleteAccessCookie() {
        return ResponseCookie.from(ACCESS_TOKEN, "")
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
                .build();
    }

    public static ResponseCookie deleteRefreshCookie() {
        return ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
                .build();
    }
}
