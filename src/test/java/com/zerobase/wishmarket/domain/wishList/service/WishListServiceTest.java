package com.zerobase.wishmarket.domain.wishList.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.model.type.UserStatusType;
import com.zerobase.wishmarket.domain.wishList.exception.WishListException;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.repository.RedisUserWishListRepository;
import com.zerobase.wishmarket.domain.wishList.repository.WishListRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WishListServiceTest {

    @InjectMocks
    private WishListService wishListService;

    @Mock
    private WishListRepository wishListRepository;

    @Mock
    private RedisUserWishListRepository redisUserWishListRepository;

    @Mock
    private ProductRepository productRepository;


    //찜목록 추가 테스트
    @Test
    void addWishListTest() {

        //given
        UserEntity user = UserEntity.builder()
            .userId(1L)
            .name("user")
            .userStatusType(UserStatusType.ACTIVE)
            .pointPrice(1000L)
            .build();

        Product product = Product.builder()
            .productId(1L)
            .name("상품")
            .price(1000L)
            .build();

        WishList wishList = WishList.builder()
            .wishListId(1L)
            .userId(user.getUserId())
            .productId(product.getProductId())
            .build();




        //조회한 찜목록의 제품Id가 999인지 확인
        Assertions.assertEquals(product.getProductId(), wishList.getProductId());

    }



    //찜목록 삭제 테스트
    @Test
    void deleteWishListTest() {
        //제품을 찜목록에 추가
        wishListService.addWishList(3L, 1L);
        wishListService.addWishList(3L, 2L);
        wishListService.addWishList(3L, 3L);

        //찜목록Id가 1인 찜목록 삭제, 제품Id가 1인 제품이 삭제됨
        wishListService.deleteWishList(3L, 1L);

        RedisUserWishList redisUserWishList = redisUserWishListRepository.findById(3L)
                .orElseThrow(() -> new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND));

        WishList wishList = WishList.builder()
            .wishListId(1L)
            .userId(1L)
            .productId(product.getProductId())
            .build();

        List<WishList> wishLists = new ArrayList<>();
        wishLists.add(wishList);

        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(product));


        given(wishListRepository.findAllByUserId(anyLong()))
            .willReturn(wishLists);


        //wishListService.addWishList(1L, product.getProductId());
        Assertions.assertThrows(WishListException.class, () -> {
            wishListService.addWishList(1L, product.getProductId());
        });
    }

    //찜목록 조회 테스트

}