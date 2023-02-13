package com.zerobase.wishmarket.domain.authcode.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class AuthException extends GlobalException {
    private final AuthErrorCode authErrorCode;

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
        this.authErrorCode = errorCode;
    }
}
