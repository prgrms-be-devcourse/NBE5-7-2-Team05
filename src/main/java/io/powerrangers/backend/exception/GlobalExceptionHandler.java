package io.powerrangers.backend.exception;

import io.powerrangers.backend.dto.BaseResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class, AuthTokenException.class})
    protected ResponseEntity<BaseResponse<Void>> handleCustomException(CustomException e) {
        return BaseResponse.error(e.getErrorCode().getMessage(), e.getErrorCode().getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        return BaseResponse.error(errorMessage, errorCode.getStatus());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    protected ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return BaseResponse.error(ErrorCode.INVALID_REQUEST.getMessage(), ErrorCode.INVALID_REQUEST.getStatus());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    protected ResponseEntity<BaseResponse<Void>> handleMissingRequestCookieException(MissingRequestCookieException e) {
        log.warn("[인증 실패] 토큰 쿠키가 존재하지 않음. 원인: {}", e.getMessage());
        return BaseResponse.error(ErrorCode.UNAUTHORIZED.getMessage(), ErrorCode.UNAUTHORIZED.getStatus());
    }

    @ExceptionHandler({IOException.class, Exception.class})
    protected ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return BaseResponse.error(errorCode.getMessage(), errorCode.getStatus());
    }
}
