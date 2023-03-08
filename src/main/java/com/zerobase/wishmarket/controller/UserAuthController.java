package com.zerobase.wishmarket.controller;

import com.zerobase.wishmarket.domain.user.model.dto.*;
import com.zerobase.wishmarket.domain.user.model.form.EmailCheckForm;
import com.zerobase.wishmarket.domain.user.model.form.RefreshForm;
import com.zerobase.wishmarket.domain.user.model.form.SignInForm;
import com.zerobase.wishmarket.domain.user.model.form.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.type.UserWithdrawalReturnType;
import com.zerobase.wishmarket.domain.user.service.UserAuthService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api("회원 인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpEmailResponse> signUpEmail(@RequestBody @Valid SignUpForm form) {
        return ResponseEntity.ok(userAuthService.signUp(form));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(
            @RequestHeader(name = "Authorization") String accessToken,
            @RequestBody RefreshForm from
    ) {
        return ResponseEntity.ok(userAuthService.reissue(accessToken, from.getRefresh()));
    }

    @PostMapping("/email-check")
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestBody @Valid EmailCheckForm from) {
        return null;
    }

    @PostMapping("/sign-in/email")
    public ResponseEntity<SignInResponse> signInEmail(@RequestBody @Valid SignInForm form) {
        return ResponseEntity.ok(userAuthService.signInEmail(form));
    }

    @GetMapping("/sign-in/social/{provider}")
    public ResponseEntity<SignInResponse> login(@PathVariable String provider, @RequestParam String code) {
        return ResponseEntity.ok(userAuthService.signInSocial(provider, code));
    }


    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@AuthenticationPrincipal Long userId,
                                                 @RequestHeader(name = "Authorization") String accessToken) {
        return ResponseEntity.ok(userAuthService.logout(userId, accessToken));
    }

    @DeleteMapping("/withdrawal")
    public UserWithdrawalReturnType withdrawal(@AuthenticationPrincipal Long userId,
                                               @RequestHeader(name = "Authorization") String accessToken) {
        return userAuthService.withdrawal(userId, accessToken);
    }
}

