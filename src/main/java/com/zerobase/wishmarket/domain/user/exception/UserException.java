package com.zerobase.wishmarket.domain.user.exception;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class UserException extends GlobalException {
    private final UserErrorCode userErrorCode;

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
        this.userErrorCode = errorCode;
    }
}
