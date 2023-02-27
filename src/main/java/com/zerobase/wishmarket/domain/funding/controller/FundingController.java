package com.zerobase.wishmarket.domain.funding.controller;

import com.zerobase.wishmarket.domain.funding.model.dto.FundingJoinResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingListGiveResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingMyGiftListResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartResponse;
import com.zerobase.wishmarket.domain.funding.model.form.FundingJoinInputForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingReceptionForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.service.FundingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/join")
    public ResponseEntity<FundingJoinResponse> joinFunding(@AuthenticationPrincipal Long userId,
        @RequestBody FundingJoinInputForm fundingJoinInputForm) {

        return ResponseEntity.ok().body(fundingService.joinFunding(userId, fundingJoinInputForm));
    }

    @PostMapping("/reception")
    public void fundingReception(@AuthenticationPrincipal Long userId, @RequestBody FundingReceptionForm form) {
        fundingService.receptionFunding(userId, form);
    }

    @GetMapping("/history")
    public ResponseEntity<List<FundingListGiveResponse>> getFundingListGive(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok().body(fundingService.getFundingListGive(userId));
    }

    @GetMapping("/gift")
    public ResponseEntity<List<FundingMyGiftListResponse>> getMyFundingGift(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(fundingService.getMyFundigGifyList(userId));
    }

}
