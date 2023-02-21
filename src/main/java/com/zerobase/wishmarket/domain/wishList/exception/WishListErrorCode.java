package com.zerobase.wishmarket.domain.wishList.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WishListErrorCode implements ErrorCode {
    WISHLIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 찜목록이 없습니다."),
    ALREADY_PUT_WISHLIST_PRODUCT(HttpStatus.BAD_REQUEST, "이미 찜목록에 저장한 상품입니다.");

    private final HttpStatus errorCode;
    private final String message;

}
