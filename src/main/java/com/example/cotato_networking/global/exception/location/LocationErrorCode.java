package com.example.cotato_networking.global.exception.location;

import com.example.cotato_networking.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LocationErrorCode implements ErrorCode {

    CONFLICT(HttpStatus.CONFLICT, "이미 존재하는 위치 이름입니다.", "LOCATION-001"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 위치를 찾을 수 없습니다.", "LOCATION-002");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
