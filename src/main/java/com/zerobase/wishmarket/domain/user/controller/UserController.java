package com.zerobase.wishmarket.domain.user.controller;


import com.zerobase.wishmarket.domain.user.model.dto.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.dto.UpdateForm;
import com.zerobase.wishmarket.domain.user.model.dto.UserInfoResponse;
import com.zerobase.wishmarket.domain.user.model.type.UserPasswordChangeReturnType;
import com.zerobase.wishmarket.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/detail")
    public ResponseEntity<UserInfoResponse> userDetail(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.userDetail(userId));
    }

    @PatchMapping("/password")
    public ResponseEntity<UserPasswordChangeReturnType> passwordChange(@RequestBody @Valid ChangePwdForm form) {
        return ResponseEntity.ok(userService.passwordChange(form));
    }

    @PatchMapping("/update")
    public ResponseEntity<UserInfoResponse> userInfoUpdate(@AuthenticationPrincipal Long userId, @RequestBody UpdateForm form) {
        return ResponseEntity.ok(userService.userInfoUpdate(userId, form));
    }
}
