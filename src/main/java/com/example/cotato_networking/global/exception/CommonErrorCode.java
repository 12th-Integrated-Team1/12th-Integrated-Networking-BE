package com.example.cotato_networking.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 에러가 발생하였습니다.", "COMMON-001"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "외부 내부에서 에러가 발생하였습니다.", "COMMON-002"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "COMMON-003"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", "COMMON-004"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다.", "COMMON-005"),

    // Weather 관련 에러 코드
    WEATHER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "날씨 정보를 가져올 수 없습니다.", "WEATHER-001")
    ;


    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}