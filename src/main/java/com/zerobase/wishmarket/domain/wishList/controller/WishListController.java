package com.zerobase.wishmarket.domain.wishList.controller;

import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.domain.wishList.model.entity.WishList;
import com.zerobase.wishmarket.domain.wishList.service.WishListService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    private final JwtAuthenticationProvider provider;


    //위시리스트 추가
    @PostMapping("/add")
    public ResponseEntity<WishList> add(@RequestHeader("Authorization") String token,
        @RequestParam Long productId) {
        String userId = provider.getUserId(token);
        return ResponseEntity.ok()
            .body(wishListService.addWishList(Long.valueOf(userId), productId));
    }

    //위시리스트 조회
    @GetMapping
    public ResponseEntity<List<WishList>> get(@RequestHeader("Authorization") String token) {
        String userId = provider.getUserId(token);

        return ResponseEntity.ok().body(wishListService.getWishList(Long.valueOf(userId)));
    }


    //위시리스트 삭제
    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestHeader("Authorization") String token,
        @RequestParam Long wishListId) {
        return ResponseEntity.ok()
            .body(wishListService.deleteWishList(wishListId));

    }

}
