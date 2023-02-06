package com.zerobase.wishmarket.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final GlobalErrorCode errorCode;

    public GlobalException(GlobalErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

}
