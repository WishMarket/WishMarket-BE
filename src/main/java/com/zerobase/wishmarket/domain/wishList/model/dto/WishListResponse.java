package com.zerobase.wishmarket.domain.wishList.model.dto;

import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishListResponse {

    private Long wishListId;

    private Long userId;

    @Nullable
    private Long fundingId;

    private Long productId;

    private String productName;

    private Long price;

    private String productImage;


    public static WishListResponse of (WishList wishList){
        return WishListResponse.builder()
            .wishListId(wishList.getWishListId())
            .userId(wishList.getUserId())
            .fundingId(wishList.getFundingId())
            .productId(wishList.getProductId())
            .productName(wishList.getProductName())
            .price(wishList.getPrice())
            .productImage(wishList.getProductImage())
            .build();

    }



}
