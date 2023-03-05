package com.zerobase.wishmarket.controller;


import com.zerobase.wishmarket.domain.user.model.dto.UserInfoResponse;
import com.zerobase.wishmarket.domain.user.model.form.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.form.UpdateForm;
import com.zerobase.wishmarket.domain.user.model.type.UserPasswordChangeReturnType;
import com.zerobase.wishmarket.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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

    @PatchMapping("/updateUserInfo")
    public ResponseEntity<UserInfoResponse> updateUserInfo(@AuthenticationPrincipal Long userId, UpdateForm form) {
        return ResponseEntity.ok(userService.updateUserInfo(userId, form));
    }

    @PatchMapping("/updateUserProfileImage")
    public ResponseEntity<UserInfoResponse> updateUserProfileImage(@AuthenticationPrincipal Long userId, MultipartFile profileImage) {
        return ResponseEntity.ok(userService.updateUserProfileImage(userId, profileImage));
    }
}
