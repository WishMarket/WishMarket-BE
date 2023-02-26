package com.zerobase.wishmarket.domain.wishList.controller;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.domain.wishList.model.dto.WishListResponse;
import com.zerobase.wishmarket.domain.wishList.service.WishListService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    private final JwtAuthenticationProvider provider;


    //위시리스트 추가
    @PostMapping("/add/{productId}")
    public ResponseEntity<WishListResponse> addWishList(@AuthenticationPrincipal Long userId,
        @PathVariable Long productId) {
        return ResponseEntity.ok()
            .body(wishListService.addWishList(userId, productId));
    }

    //위시리스트 조회
    @GetMapping
    public ResponseEntity<List<WishListResponse>> getWishList(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok().body(wishListService.getWishList(userId));
    }

    //위시리스트 삭제
    @DeleteMapping("{wishListId}")
    public ResponseEntity<Boolean> deleteWishList(@AuthenticationPrincipal Long userId,
        @PathVariable Long wishListId) {
        return ResponseEntity.ok()
            .body(wishListService.deleteWishList(userId, wishListId));

    }

    //친구의 위시리스트 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<WishListResponse>> getUserWishList(@PathVariable Long userId) {
        return ResponseEntity.ok()
            .body(wishListService.getUserWishList(userId));
    }

}
