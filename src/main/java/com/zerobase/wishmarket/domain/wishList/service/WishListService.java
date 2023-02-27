package com.zerobase.wishmarket.domain.wishList.service;

import com.zerobase.wishmarket.domain.product.exception.ProductErrorCode;
import com.zerobase.wishmarket.domain.product.exception.ProductException;
import com.zerobase.wishmarket.domain.product.model.entity.Product;
import com.zerobase.wishmarket.domain.product.repository.ProductRepository;
import com.zerobase.wishmarket.domain.wishList.exception.WishListErrorCode;
import com.zerobase.wishmarket.domain.wishList.exception.WishListException;
import com.zerobase.wishmarket.domain.wishList.model.dto.WishListResponse;
import com.zerobase.wishmarket.domain.wishList.model.entity.RedisUserWishList;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.repository.RedisUserWishListRepository;
import com.zerobase.wishmarket.domain.wishList.repository.WishListRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class WishListService {

    private final ProductRepository productRepository;

    private final WishListRepository wishListRepository;

    private final RedisUserWishListRepository redisUserWishListRepository;

    @Transactional
    public WishListResponse addWishList(Long userId, Long productId) {

        //사용자의 찜목록에 이미 추가한 상품인지 확인
        List<WishList> userWishList = wishListRepository.findAllByUserId(userId);

        Product product = productRepository.findById(productId)
            .orElseThrow(()-> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (!userWishList.isEmpty()) {
            for (WishList ws : userWishList) {
                if (productId == ws.getProductId()) {
                    throw new WishListException(WishListErrorCode.ALREADY_PUT_WISHLIST_PRODUCT);
                }
            }
        }

        //추가할 위시리스트 저장
        userWishList.add(wishListRepository.save(WishList.builder()
            .userId(userId)
            .productId(productId)
            .productName(product.getName())
            .price(product.getPrice())
            .productImage(product.getProductImage())
            .build()));

        //레디스 업데이트
        redisUserWishListRepository.save(RedisUserWishList.builder()
            .id(userId)
            .wishLists(userWishList)
            .build());

        return WishListResponse.of(userWishList.get(userWishList.size()-1));
    }




    public List<WishListResponse> getWishList(Long userId) {

        Optional<RedisUserWishList> redisUserWishList = redisUserWishListRepository.findById(userId);
        List<WishListResponse> wishListResponseList = new ArrayList<>();

        //아직 생성된 캐쉬가 없는 경우
        if (redisUserWishList.isEmpty()) {
            List<WishList> wishLists = wishListRepository.findAllByUserId(userId);
            for(WishList wishList : wishLists){
                WishListResponse wishListResponse = WishListResponse.of(wishList);
                wishListResponseList.add(wishListResponse);
            }
            return wishListResponseList;
        }

        //캐쉬가 있는 경우
        List<WishList> wishLists = redisUserWishList.get().getWishLists();
        for(WishList wishList : wishLists){
            WishListResponse wishListResponse = WishListResponse.of(wishList);
            wishListResponseList.add(wishListResponse);
        }
        return wishListResponseList;
    }

    @Transactional
    public boolean deleteWishList(Long userId, Long productId) {
        WishList wishList = wishListRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND));

        RedisUserWishList redisUserWishList = redisUserWishListRepository.findById(userId)
            .orElseThrow(() -> new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND));

        wishListRepository.delete(wishList);

        //레디스도 업데이트
        List<WishList> updatedUserWishList = wishListRepository.findAllByUserId(userId);
        redisUserWishListRepository.save(RedisUserWishList.builder()
            .id(userId)
            .wishLists(updatedUserWishList)
            .build());

        return true;
    }


}
