package com.zerobase.wishmarket.domain.alarm.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class AlarmException extends GlobalException {

    private final AlarmErrorCode alarmErrorCode;

    public AlarmException(AlarmErrorCode errorCode) {
        super(errorCode);
        this.alarmErrorCode = errorCode;
    }
}

