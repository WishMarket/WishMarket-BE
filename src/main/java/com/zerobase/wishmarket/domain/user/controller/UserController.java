package com.zerobase.wishmarket.domain.user.controller;


import com.zerobase.wishmarket.domain.user.model.dto.ChangePwdForm;
import com.zerobase.wishmarket.domain.user.model.dto.UserDto;
import com.zerobase.wishmarket.domain.user.model.type.UserPasswordChangeReturnType;
import com.zerobase.wishmarket.domain.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/detail")
    public ResponseEntity<?> userDetail(@AuthenticationPrincipal Long userId) {
        UserDto userInfo = userService.userDetail(userId);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/password")
    public ResponseEntity<UserPasswordChangeReturnType> passwordChange(@RequestBody @Valid ChangePwdForm form) {
        return ResponseEntity.ok(userService.passwordChange(form));
    }
}
