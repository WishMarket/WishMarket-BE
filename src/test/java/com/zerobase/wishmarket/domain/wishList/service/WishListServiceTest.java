package com.zerobase.wishmarket.domain.wishList.service;

import com.zerobase.wishmarket.domain.product.exception.ProductNotFoundException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.wishList.exception.WishListErrorCode;
import com.zerobase.wishmarket.domain.wishList.exception.WishListException;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.repository.WishListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class WishListServiceTest {

    @Autowired
    private WishListService wishListService;

    @Autowired
    private WishListRepository wishListRepository;


    //찜목록 추가 테스트
    @Test
    void addWishListTest() {
        wishListService.addWishList(1L, 10L);

        Optional<WishList> wishList = wishListRepository.findByWishListId(1L);
        if (wishList.isEmpty()) {
            throw new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND);
        }

        Assertions.assertEquals(10L, wishList.get().getProductId());

    }

    //찜목록 추가 같은 상품을 넣을시 예외처리 발생 테스트
    @Test
    void addWishListExceptionTest() {
        wishListService.addWishList(1L, 10L);
        Assertions.assertThrows(WishListException.class, () ->{
            wishListService.addWishList(1L,10L);
        });
    }


}