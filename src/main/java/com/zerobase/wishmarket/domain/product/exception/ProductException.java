package com.zerobase.wishmarket.domain.product.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class ProductException extends GlobalException {

    private final ProductErrorCode productErrorCode;

    protected ProductException(ProductErrorCode errorCode) {
        super(errorCode);
        this.productErrorCode = errorCode;
    }
}
