package com.zerobase.wishmarket.domain.product.exception;


import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당하는 상품이 존재하지 않습니다."),
    BEST_PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "베스트 상품을 불러오는데 실패하였습니다."),


    ;

    private final HttpStatus errorCode;
    private final String message;

}
