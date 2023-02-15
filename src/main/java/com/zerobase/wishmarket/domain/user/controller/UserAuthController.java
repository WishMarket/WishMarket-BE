package com.zerobase.wishmarket.domain.user.controller;

import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.SignInForm;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.service.UserAuthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final RedirectView redirectView;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpEmailResponse> signUpEmail(@RequestBody @Valid SignUpForm form) {
        return ResponseEntity.ok(userAuthService.signUp(form));
    }

    @PostMapping("/sign-in/email")
    public ResponseEntity<?> signInEmail(@RequestBody @Valid SignInForm form) {
        return ResponseEntity.ok(userAuthService.signInEmail(form));
    }

    @PostMapping("/sign-in/google")
    public ResponseEntity<?> signInGoogle(@LoginUserInfo OAuthUserInfo userInfo) {
        redirectView.setUrl("/oauth2/authorization/google");
        return ResponseEntity.ok(userAuthService.signInSocial(userInfo));
    }

    @PostMapping("/sign-in/naver")
    public ResponseEntity<?> signInNaver(@LoginUserInfo OAuthUserInfo userInfo) {
        redirectView.setUrl("/oauth2/authorization/naver");
        return ResponseEntity.ok(userAuthService.signInSocial(userInfo));
    }
}
