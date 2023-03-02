package com.zerobase.wishmarket.domain.alarm.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AlarmErrorCode implements ErrorCode {
    ALARM_NOT_FOUND(HttpStatus.BAD_REQUEST,"존재하지 않는 알람입니다."),
    ALARM_IS_EMPTY(HttpStatus.BAD_REQUEST,"알람이 비어있습니다."),
    ALARM_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "알람이 연결에 실패하였습니다." );

    private final HttpStatus errorCode;
    private final String message;

}
