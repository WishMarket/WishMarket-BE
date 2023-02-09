package com.zerobase.wishmarket.domain.user.controller;

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
public class AuthController {

    private final HttpSession httpSession;

    @GetMapping("/social-sign-in")
    public String oauthLoginInfo(Model model) {

        OAuthUserInfo user = (OAuthUserInfo) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "oauthLoginInfo";
    }
}
