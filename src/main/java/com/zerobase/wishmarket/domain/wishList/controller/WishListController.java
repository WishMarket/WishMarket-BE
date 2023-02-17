package com.zerobase.wishmarket.domain.wishList.controller;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    private final JwtAuthenticationProvider provider;


    //위시리스트 추가
    @PostMapping("/add")
    public ResponseEntity<WishList> addWishList(@AuthenticationPrincipal Long userId,
                                                @RequestParam Long productId) {
        return ResponseEntity.ok()
                .body(wishListService.addWishList(userId, productId));
    }

    //위시리스트 조회
    @GetMapping
    public ResponseEntity<List<WishList>> getWishList(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok().body(wishListService.getWishList(userId));
    }

    //위시리스트 삭제
    @DeleteMapping
    public ResponseEntity<Boolean> deleteWishList(@AuthenticationPrincipal Long userId,
                                                  @RequestParam Long wishListId) {
        return ResponseEntity.ok()
                .body(wishListService.deleteWishList(userId, wishListId));

    }

}
