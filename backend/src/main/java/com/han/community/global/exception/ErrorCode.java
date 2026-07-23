package com.han.community.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생하였습니다."),

    // 인증
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다. 관리자에게 문의하세요."),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재비밀번호가 일치하지 않습니다."),
    SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호는 현재 비밀번호와 달라야 합니다."),

    // 채널
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채널입니다."),

    // 게시글
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),

    // 댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다.")
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
