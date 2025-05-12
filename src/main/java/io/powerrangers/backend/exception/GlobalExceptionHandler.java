package io.powerrangers.backend.exception;

import io.powerrangers.backend.global.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<BaseResponse<?>> handleCustomException(CustomException e) {
        return BaseResponse.error(e.getErrorCode().getMessage(), HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        return BaseResponse.error(errorCode.getMessage(), HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    protected ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        return BaseResponse.error(errorCode.getMessage(), HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return BaseResponse.error(errorCode.getMessage(), HttpStatus.valueOf(errorCode.getStatus()));
    }
}
