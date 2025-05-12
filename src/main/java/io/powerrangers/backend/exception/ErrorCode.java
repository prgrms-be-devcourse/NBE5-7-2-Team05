package io.powerrangers.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_REQUEST(400, "잘못된 입력값입니다."),

    // 403 Forbidden
    NOT_THE_OWNER(403,"할 일에 대한 권한이 없습니다."),

    // 404 Not Found
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    TASK_NOT_FOUND(404, "존재하지 않는 할 일입니다."),
    COMMENT_NOT_FOUND(404, "존재하지 않는 댓글입니다."),
    FOLLOW_NOT_FOUND(404, "팔로우 관계를 찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATED_NICKNAME(409, "이미 존재하는 닉네임입니다."),
    ALREADY_FOLLOWED(409, "이미 팔로우한 사용자입니다."),

    // 500 INTERNAL_SERVER ERROR
    INTERNAL_SERVER_ERROR(500,"서버 에러입니다. 서버 팀에게 문의해주세요.");

    private final int status;
    private final String message;

}
