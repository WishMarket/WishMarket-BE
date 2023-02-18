package com.zerobase.wishmarket.domain.user.controller;

import com.zerobase.wishmarket.domain.user.config.LoginUserInfo;
import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

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