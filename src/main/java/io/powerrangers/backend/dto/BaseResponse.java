package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/***
 * ToDo: 에러메시지는 반환, 성공메시지를 삭제하기위해선 두가지 방법이 있어보인다.
 * 1. message필드를 제외한 성공 리스폰스와, 실패 리스폰스를 따로 관리.
 * 2. 필드를 유지하고 통합하여 관리하지만 성공 시 message null값 처리.
 * 일단은 2번이 더 적합하다고 판단하여 진행하였습니다!
 ****/
@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    //성공 메시지만 반환
    public static ResponseEntity<BaseResponse<?>> success(HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), null, null));
    }

    //성공 메시지와 데이터 반환
    public static <T> ResponseEntity<BaseResponse<T>> success(HttpStatus status, T data) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), null, data));
    }

    //에러 메시지만 반환
    public static ResponseEntity<BaseResponse<?>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), message, null));
    }
    //에러 메시지와 데이터를 반환
    public static <T> ResponseEntity<BaseResponse<T>> error(String message, T data, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), message, data));
    }
}