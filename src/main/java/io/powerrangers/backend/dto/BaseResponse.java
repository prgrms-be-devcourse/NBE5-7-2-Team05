package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    //성공 메시지만 반환
    public static ResponseEntity<BaseResponse<Void>> success(SuccessCode successCode) {
        return ResponseEntity.status(successCode.getStatus())
                .body(new BaseResponse<>(successCode.getStatus().value(), successCode.getMessage(), null));
    }

    //성공 메시지와 데이터 반환
    public static <T> ResponseEntity<BaseResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity.status(successCode.getStatus())
                .body(new BaseResponse<>(successCode.getStatus().value(), successCode.getMessage(), data));
    }

    //에러 메시지만 반환
    public static ResponseEntity<BaseResponse<Void>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), message, null));
    }
    //에러 메시지와 데이터를 반환
    public static <T> ResponseEntity<BaseResponse<T>> error(String message, T data, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), message, data));
    }
}