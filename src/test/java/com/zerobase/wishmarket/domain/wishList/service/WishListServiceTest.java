package com.zerobase.wishmarket.domain.wishList.service;

import com.zerobase.wishmarket.domain.wishList.exception.WishListErrorCode;
import com.zerobase.wishmarket.domain.wishList.exception.WishListException;
import com.zerobase.wishmarket.domain.wishList.model.entity.RedisUserWishList;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.repository.RedisUserWishListRepository;
import com.zerobase.wishmarket.domain.wishList.repository.WishListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@TestPropertySource("classpath:application.properties")
@SpringBootTest
class WishListServiceTest {

    @Autowired
    private WishListService wishListService;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private RedisUserWishListRepository redisUserWishListRepository;


    //찜목록 추가 테스트
    @Test
    void addWishListTest() {
        //제품Id가 10인 제품을 찜목록에 추가
        wishListService.addWishList(1L, 10L);

        Optional<WishList> wishList = wishListRepository.findByWishListId(1L);
        if (wishList.isEmpty()) {
            throw new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND);
        }

        //조회한 찜목록의 제품Id가 10인지 확인
        Assertions.assertEquals(10L, wishList.get().getProductId());

    }

    //찜목록 추가 같은 상품을 넣을시 예외처리 발생 테스트
    @Test
    void addWishListExceptionTest() {
        wishListService.addWishList(1L, 10L);
        Assertions.assertThrows(WishListException.class, () -> {
            wishListService.addWishList(1L, 10L);
        });
    }

    //찜목록 조회 테스트
    @Test
    void getWishListTest() {
        //제품Id가 10인 제품을 찜목록에 추가
        wishListService.addWishList(1L, 10L);
        RedisUserWishList redisUserWishList = redisUserWishListRepository.findById(1L)
                .orElseThrow(() -> new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND));

        //redis에 저장된 제품 번호가 10이 맞는지 확인
        Assertions.assertEquals(10L, redisUserWishList.getWishLists().get(0).getProductId());
    }

    //찜목록 삭제 테스트
    @Test
    void deleteWishListTest() {
        //제품을 찜목록에 추가
        wishListService.addWishList(1L, 1L);
        wishListService.addWishList(1L, 2L);
        wishListService.addWishList(1L, 3L);

        //찜목록Id가 1인 찜목록 삭제, 제품Id가 1인 제품이 삭제됨
        wishListService.deleteWishList(1L, 1L);

        RedisUserWishList redisUserWishList = redisUserWishListRepository.findById(1L)
                .orElseThrow(() -> new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND));

        Assertions.assertNotEquals(1L,redisUserWishList.getWishLists().get(0).getProductId());

    }

}