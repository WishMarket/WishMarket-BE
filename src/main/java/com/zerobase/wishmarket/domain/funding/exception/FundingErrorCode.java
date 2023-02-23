package com.zerobase.wishmarket.domain.funding.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FundingErrorCode implements ErrorCode {
    FUNDING_START_FAIL(HttpStatus.BAD_REQUEST, "펀딩 시작을 실패하였습니다."),
    FUNDING_JOIN_FAIL(HttpStatus.BAD_REQUEST, "펀딩 참여를 실패하였습니다."),
    FUNDING_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 펀딩이 없습니다.")

    ;

    private final HttpStatus errorCode;
    private final String message;
}
