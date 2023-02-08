package com.zerobase.wishmarket.domain.product.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("p01","해당하는 상품이 존재하지 않습니다.")

    ;

    private final String code;
    private final String message;


}
