package com.zerobase.wishmarket.domain.wishList.service;

import com.zerobase.wishmarket.domain.wishList.exception.WishListErrorCode;
import com.zerobase.wishmarket.domain.wishList.exception.WishListException;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.repository.WishListRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;


    public WishList addWishList(Long userId, Long productId) {

        //사용자의 찜목록이 존재하는 경우, 이미 추가한 상품인지 확인
        List<WishList> wishLists = wishListRepository.findAllByUserId(userId);

        if (wishLists.isEmpty()) {
            for (WishList ws : wishLists) {
                if (productId == ws.getProductId()) {
                    throw new WishListException(WishListErrorCode.ALREADY_PUT_WISHLIST_PRODUCT);
                }
            }
        }

        return wishListRepository.save(WishList.builder()
            .userId(userId)
            .productId(productId)
            .build());
    }

    //페이징 처리 여부 필요
    //redis 활용 고려
    public List<WishList> getWishList(Long userId) {
        return wishListRepository.findAllByUserId(userId);
    }


    public boolean deleteWishList(Long wishListId) {
        WishList wishList = wishListRepository.findByWishListId(wishListId)
            .orElseThrow(() -> new WishListException(WishListErrorCode.WISHLIST_NOT_FOUND));
        wishListRepository.delete(wishList);
        return true;
    }

}
