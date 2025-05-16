package io.powerrangers.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@Getter
@AllArgsConstructor
public class AuthTokenException extends RuntimeException {
    private final ErrorCode errorCode;
    private String provider = null;

    public AuthTokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
