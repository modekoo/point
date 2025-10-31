package com.musinsa.point.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "정책이 존재하지 않습니다."),
    POLICY_OVER_EARN(HttpStatus.BAD_REQUEST, "1회 한도 적립 포인트를 초과했습니다."),
    POLICY_OVER_MAX(HttpStatus.BAD_REQUEST, "보유 적립 포인트를 초과했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
