package com.zerobase.wishmarket.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException{

    private final ErrorCode errorCode;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }
}
