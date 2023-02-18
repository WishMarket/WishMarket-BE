package com.zerobase.wishmarket.domain.point.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements ErrorCode {

    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),

    ;

    private final HttpStatus errorCode;
    private final String message;
}
