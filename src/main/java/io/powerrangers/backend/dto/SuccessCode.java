package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // 200 OK
    MODIFIED_SUCCESS(HttpStatus.OK, "수정이 성공적으로 수행되었습니다."),
    GET_SUCCESS(HttpStatus.OK, "데이터를 성공적으로 가져왔습니다."),

    // 201 Created
    ADDED_SUCCESS(HttpStatus.CREATED, "데이터가 성공적으로 추가되었습니다."),

    // 204 No Content
    DELETED_SUCCESS(HttpStatus.NO_CONTENT, "삭제가 성공적으로 수행되었습니다.");

    private final HttpStatus status;
    private final String message;

}
