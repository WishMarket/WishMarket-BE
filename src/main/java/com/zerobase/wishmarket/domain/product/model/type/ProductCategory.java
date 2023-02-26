package com.zerobase.wishmarket.domain.product.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductCategory {
    CLOTHES(1), HOME_ELECTRONICS(2), TOY(3),
    IT_DEVICE(4), JEWELRY(5), FURNITURE(6),
    ETC(7);

    private final int categoryCode;

}