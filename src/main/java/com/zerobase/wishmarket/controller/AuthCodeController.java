package com.zerobase.wishmarket.controller;

import com.zerobase.wishmarket.domain.authcode.model.form.AuthCodeMailForm;
import com.zerobase.wishmarket.domain.authcode.model.dto.AuthCodeResponse;
import com.zerobase.wishmarket.domain.authcode.model.form.AuthCodeVerifyForm;
import com.zerobase.wishmarket.domain.authcode.service.AuthCodeService;
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
public class AuthCodeController {

    private final AuthCodeService authCodeService;

    @PostMapping
    public ResponseEntity<AuthCodeResponse> authCodeMailSend(@RequestBody @Valid AuthCodeMailForm form) {
        return ResponseEntity.ok(authCodeService.sendAuthCode(form));
    }

    @PostMapping("/code")
    public ResponseEntity<AuthCodeResponse> authCodeVerify(@RequestBody @Valid AuthCodeVerifyForm form) {
        return ResponseEntity.ok(authCodeService.authCodeVerify(form));
    }
}
