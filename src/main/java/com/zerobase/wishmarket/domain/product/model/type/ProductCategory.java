package com.zerobase.wishmarket.domain.product.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductCategory {
    CLOTHES(0), HOME_ELECTRONICS(1), TOY(2),
    IT_DEVICE(3), JEWELRY(4), FURNITURE(5),
    ETC(6);

    private final int categoryCode;

}