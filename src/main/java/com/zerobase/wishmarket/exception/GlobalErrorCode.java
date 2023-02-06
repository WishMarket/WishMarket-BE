package com.zerobase.wishmarket.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum GlobalErrorCode {

    //로그인 관련
    THERE_IS_NO_TOKEN(HttpStatus.BAD_REQUEST, "로그인이 필요합니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "기간이 만료된 토큰입니다. 다시 로그인해주세요."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "타당하지 않은 토큰입니다."),


    //요청 관련
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR(HttpStatus.BAD_REQUEST, "요청 URL을 확인해주세요."),

    ;

    private final HttpStatus httpStatus;
    private final String detail;

}
