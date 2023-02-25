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
    CANNOT_BE_RECEIVED_PRODUCT(HttpStatus.BAD_REQUEST, "금액 충족이 되지 않아 수령할 수 없는 상품입니다. 관리자에게 문의하세요"),
    FUNDING_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 펀딩이 없습니다."),
    FUNDING_TARGET_NOT_FOUND(HttpStatus.BAD_REQUEST, "펀딩 대상자가 존재하지 않습니다."),
    FUNDING_TOO_MUCH_POINT(HttpStatus.BAD_REQUEST, "펀딩하려는 금액이 너무 많습니다.");


    private final HttpStatus errorCode;
    private final String message;
}
