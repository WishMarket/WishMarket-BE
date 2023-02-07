package com.zerobase.wishmarket.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException{

    private final ErrorCode errorCode;

    protected GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }
}
