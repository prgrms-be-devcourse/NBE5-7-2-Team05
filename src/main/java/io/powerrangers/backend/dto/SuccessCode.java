package io.powerrangers.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // 200 OK
    MODIFIED_SUCCESS(HttpStatus.OK),
    GET_SUCCESS(HttpStatus.OK),

    // 201 Created
    ADDED_SUCCESS(HttpStatus.CREATED),

    // 204 No Content
    DELETED_SUCCESS(HttpStatus.NO_CONTENT);

    private final HttpStatus status;

}
