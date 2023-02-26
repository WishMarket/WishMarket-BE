package com.zerobase.wishmarket.domain.wishList.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class WishListException extends GlobalException {

    private final WishListErrorCode wishListErrorCode;

    public WishListException(WishListErrorCode errorCode) {
        super(errorCode);
        this.wishListErrorCode = errorCode;
    }
}
