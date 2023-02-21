package com.zerobase.wishmarket.domain.user.controller;

import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.service.UserAuthService;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestBody @Valid EmailCheckForm from){
        return ResponseEntity.ok(userAuthService.emailCheck(from));
    }

    @PostMapping("/sign-in/email")
    public ResponseEntity<?> signInEmail(@RequestBody @Valid SignInForm form) {
        return ResponseEntity.ok(userAuthService.signInEmail(form));
    }

    @PostMapping("/sign-in/google")
    public ResponseEntity<?> signInGoogle(@LoginUserInfo OAuthUserInfo userInfo) {
        return ResponseEntity.ok(userAuthService.signInSocial(userInfo));
    }

    @PostMapping("/sign-in/naver")
    public ResponseEntity<?> signInNaver(@LoginUserInfo OAuthUserInfo userInfo) {
        return ResponseEntity.ok(userAuthService.signInSocial(userInfo));
    }

    @PostMapping("/logout")
    public void logout(SignInResponse signInResponse) {
        userAuthService.logout(signInResponse);
    }
}
