package com.zerobase.wishmarket.domain.follow.controller;

import com.zerobase.wishmarket.domain.follow.service.FollowService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{followId}")
    public void userFollowList() {

    }
}
