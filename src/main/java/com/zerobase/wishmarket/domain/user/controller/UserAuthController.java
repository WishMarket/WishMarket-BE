package com.zerobase.wishmarket.domain.user.controller;

import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.EmailCheckForm;
import com.zerobase.wishmarket.domain.user.model.dto.EmailCheckResponse;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.RefreshForm;
import com.zerobase.wishmarket.domain.user.model.dto.ReissueResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignInForm;
import com.zerobase.wishmarket.domain.user.model.dto.SignInResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailResponse;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.service.UserAuthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PostMapping("/sign-in/google")
    public ResponseEntity<SignInResponse> signInGoogle(Model model, @LoginUserInfo OAuthUserInfo userInfo) {
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
    public void logout(@AuthenticationPrincipal Long userId,
        @RequestHeader(name = "Authorization") String accessToken) {
        userAuthService.logout(userId, accessToken);
    }
}
