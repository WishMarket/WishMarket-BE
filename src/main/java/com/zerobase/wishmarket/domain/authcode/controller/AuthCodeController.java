package com.zerobase.wishmarket.domain.authcode.controller;

import com.zerobase.wishmarket.domain.authcode.model.dto.AuthCodeMailForm;
import com.zerobase.wishmarket.domain.authcode.model.dto.AuthCodeVerifyForm;
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
    public ResponseEntity<String> authCodeMailSend(@RequestBody @Valid AuthCodeMailForm form){
        authCodeService.sendAuthCode(form);
        return ResponseEntity.ok("메일 전송에 성공하였습니다.");
    }

    @PostMapping("/code")
    public ResponseEntity<Void> authCodeVerify(@RequestBody @Valid AuthCodeVerifyForm form){
        authCodeService.authCodeVerify(form);
        return ResponseEntity.ok().build();
    }
}
