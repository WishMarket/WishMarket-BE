package com.zerobase.wishmarket.domain.user.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 형식입니다."),
    ALREADY_USING_EMAIL(HttpStatus.BAD_REQUEST, "사용 중인 이메일입니다."),
    ALREADY_REGISTER_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다."),
    EMAIL_NOT_FOUND(BAD_REQUEST, "존재하지 않는 Email 입니다."),
    PASSWORD_DO_NOT_MATCH(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    CANNOT_WITHDRAW_YOU_ARE_TARGET_TO_ONGOING_FUNDING(HttpStatus.BAD_REQUEST, "진행중인 펀딩의 대상일 경우 탈퇴할 수 없습니다."),
    YOUR_GIFT_HAS_NOT_BEEN_PROCESSED(HttpStatus.BAD_REQUEST, "받은 선물함 중 미수령한 선물이 있을 경우 탈퇴할 수 없습니다.")

    ;

    private final HttpStatus errorCode;
    private final String message;
}
