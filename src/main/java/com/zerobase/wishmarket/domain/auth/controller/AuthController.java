package com.zerobase.wishmarket.domain.auth.controller;

import com.zerobase.wishmarket.domain.auth.model.dto.AuthCodeMailForm;
import com.zerobase.wishmarket.domain.auth.model.dto.AuthCodeVerifyForm;
import com.zerobase.wishmarket.domain.auth.service.AuthService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/email-auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Void> authCodeMailSend(@RequestBody @Valid AuthCodeMailForm form){
        authService.sendAuthCode(form);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/code")
    public ResponseEntity<Void> authCodeVerify(@RequestBody @Valid AuthCodeVerifyForm form){
        authService.authCodeVerify(form);
        return ResponseEntity.ok().build();
    }
}
