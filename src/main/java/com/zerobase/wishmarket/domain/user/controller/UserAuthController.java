package com.zerobase.wishmarket.domain.user.controller;

import com.zerobase.wishmarket.domain.user.annotation.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpForm;
import com.zerobase.wishmarket.domain.user.model.dto.SignUpEmailDto;
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


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpEmailDto> signUpEmail(@RequestBody @Valid SignUpForm form) {
        return ResponseEntity.ok(userAuthService.signUp(form));
    }

    // UserAuthContoller
    @GetMapping("/social-sign-in")
    // 기존 : httpSession.getAttribute 로 가져오던 세션 정보
    // 수정 : 어느 컨트롤러에서든 @LoginUserInfo 를 사용하여 세션 정보 활용 가능
    public String oauthLoginInfo(Model model, @LoginUserInfo OAuthUserInfo user) {

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "oauthLoginInfo";
    }
}
