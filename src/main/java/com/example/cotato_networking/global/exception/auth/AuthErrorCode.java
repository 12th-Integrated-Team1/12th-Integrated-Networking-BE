package com.example.cotato_networking.global.exception.auth;

import com.example.cotato_networking.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.", "AUTH-001"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "AUTH-002");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
