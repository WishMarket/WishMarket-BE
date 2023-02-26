package com.zerobase.wishmarket.domain.wishList.service;

import com.zerobase.wishmarket.domain.wishList.exception.WishListErrorCode;
import com.zerobase.wishmarket.domain.wishList.exception.WishListException;
import com.zerobase.wishmarket.domain.wishList.model.entity.RedisUserWishList;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.repository.RedisUserWishListRepository;
import com.zerobase.wishmarket.domain.wishList.repository.WishListRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final RedisUserWishListRepository redisUserWishListRepository;


    public WishList addWishList(Long userId, Long productId) {

        //사용자의 찜목록에 이미 추가한 상품인지 확인
        List<WishList> userWishList = wishListRepository.findAllByUserId(userId);

        if (!userWishList.isEmpty()) {
            for (WishList ws : userWishList) {
                if (productId == ws.getProductId()) {
                    throw new WishListException(WishListErrorCode.ALREADY_PUT_WISHLIST_PRODUCT);
                }
            }
        }

        //추가할 위시리스트 저장
        WishList wishList = wishListRepository.save(WishList.builder()
            .userId(userId)
            .productId(productId)
            .build());

        userWishList.add(wishList);

        //레디스 업데이트
        redisUserWishListRepository.save(RedisUserWishList.builder()
            .id(userId)
            .wishLists(userWishList)
            .build());

        return wishList;
    }


    public List<WishList> getWishList(Long userId) {
        Optional<RedisUserWishList> redisUserWishList = redisUserWishListRepository.findById(userId);

        //아직 생성된 캐쉬가 없는 경우
        if (redisUserWishList.isEmpty()) {
            return wishListRepository.findAllByUserId(userId);
        }

        //캐쉬가 있는 경우
        return redisUserWishList.get().getWishLists();
    }


    public boolean deleteWishList(Long userId, Long wishListId) {
        WishList wishList = wishListRepository.findByWishListId(wishListId)
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
