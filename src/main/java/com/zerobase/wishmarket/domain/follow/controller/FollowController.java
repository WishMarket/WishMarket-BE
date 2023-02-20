package com.zerobase.wishmarket.domain.follow.controller;

import com.zerobase.wishmarket.domain.follow.model.dto.UserFollowersResponse;
import com.zerobase.wishmarket.domain.follow.model.dto.UserSearchResponse;
import com.zerobase.wishmarket.domain.follow.service.FollowService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/api/follow")
@RestController
public class FollowController {

    private final FollowService followService;

    @ApiOperation("팔로우 누름")
    @PostMapping("/{followId}")
    public Boolean followUser(@AuthenticationPrincipal Long userId, @PathVariable Long followId) {
        return followService.followUser(userId, followId);
    }

    @DeleteMapping("/{followId}")
    public Boolean unFollowUser(@AuthenticationPrincipal Long userId, @PathVariable Long followId) {
        return followService.unFollowUser(userId, followId);
    }

    @GetMapping
    public ResponseEntity<List<UserSearchResponse>> getSearchUser(
        @AuthenticationPrincipal Long userId,
        @RequestParam @Nullable String email,
        @RequestParam @Nullable String name,
        @RequestParam @Nullable String nickName,
        @RequestParam Integer page
    ) {
        return ResponseEntity.ok(followService.searchUser(userId, email, name, nickName, page));
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserFollowersResponse>> getMyFollowerList(@AuthenticationPrincipal Long userId, Pageable pageable) {
        return ResponseEntity.ok(followService.getMyFollowerList(userId, pageable));
    }
}
