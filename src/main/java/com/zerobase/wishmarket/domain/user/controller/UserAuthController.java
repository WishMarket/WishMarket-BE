package com.zerobase.wishmarket.domain.user.controller;

import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.EmailCheckForm;
import com.zerobase.wishmarket.domain.user.model.dto.EmailCheckResponse;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.SignInForm;
import com.zerobase.wishmarket.domain.user.model.dto.SignInResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.service.UserAuthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpEmailResponse> signUpEmail(@RequestBody @Valid SignUpForm form) {
        return ResponseEntity.ok(userAuthService.signUp(form));
    }

    @PostMapping("/email-check")
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestBody @Valid EmailCheckForm from) {
        return ResponseEntity.ok(userAuthService.emailCheck(from));
    }

    @PostMapping("/sign-in/email")
    public ResponseEntity<?> signInEmail(@RequestBody @Valid SignInForm form) {
        return ResponseEntity.ok(userAuthService.signInEmail(form));
    }

    @PostMapping("/sign-in/google")
    public ResponseEntity<?> signInGoogle(Model model, @LoginUserInfo OAuthUserInfo userInfo) {
        if (userInfo != null) {
            SignInResponse loginUserInfo = userAuthService.signInGoogle(userInfo);
            model.addAttribute("loginUserInfo", loginUserInfo);
        }
        return ResponseEntity.ok(userAuthService.signInGoogle(userInfo));
    }

    @PostMapping("/sign-in/naver")
    public ResponseEntity<SignInResponse> signInNaver(Model model, @LoginUserInfo OAuthUserInfo userInfo) {
        if (userInfo != null) {
            SignInResponse loginUserInfo = userAuthService.signInNaver(userInfo);
            model.addAttribute("loginUserInfo", loginUserInfo);
        }
        return ResponseEntity.ok(userAuthService.signInNaver(userInfo));
    }

    @PostMapping("/logout")
    public void logout(SignInResponse signInResponse) {
        userAuthService.logout(signInResponse);
    }
}
