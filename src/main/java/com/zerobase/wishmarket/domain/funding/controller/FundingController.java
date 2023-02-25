package com.zerobase.wishmarket.domain.funding.controller;

import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartResponse;
import com.zerobase.wishmarket.domain.funding.model.form.FundingReceptionForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/funding")
@RestController
public class FundingController {

    private final FundingService fundingService;

    @PostMapping("/start")
    public ResponseEntity<FundingStartResponse> startFunding(@AuthenticationPrincipal Long userId,
        @RequestBody FundingStartInputForm fundingStartInputForm) {

        return ResponseEntity.ok().body(fundingService.startFunding(userId, fundingStartInputForm));
    }

    @PostMapping("/reception")
    public void fundingReception(@AuthenticationPrincipal Long userId, @RequestBody FundingReceptionForm form){
        fundingService.receptionFunding(userId, form);
    }

}
