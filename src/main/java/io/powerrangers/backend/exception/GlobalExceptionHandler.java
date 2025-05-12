package io.powerrangers.backend.exception;

import io.powerrangers.backend.dto.BaseResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<BaseResponse<?>> handleCustomException(CustomException e) {
        return BaseResponse.error(e.getErrorCode().getMessage(), e.getErrorCode().getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        return BaseResponse.error(errorMessage, errorCode.getStatus());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    protected ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return BaseResponse.error(ErrorCode.INVALID_REQUEST.getMessage(), ErrorCode.INVALID_REQUEST.getStatus());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return BaseResponse.error(errorCode.getMessage(), errorCode.getStatus());
    }
}
