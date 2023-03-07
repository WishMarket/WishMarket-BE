package com.zerobase.wishmarket.controller;

import com.zerobase.wishmarket.domain.funding.model.dto.FundingDetailResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingJoinResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingListFriendResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingListGiveResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingMyGiftListResponse;
import com.zerobase.wishmarket.domain.funding.model.dto.FundingStartResponse;
import com.zerobase.wishmarket.domain.funding.model.form.FundingJoinInputForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingReceptionForm;
import com.zerobase.wishmarket.domain.funding.model.form.FundingStartInputForm;
import com.zerobase.wishmarket.domain.funding.service.FundingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    //PR 재요청
    @GetMapping("/history/{friendId}")
    public ResponseEntity<List<FundingListFriendResponse>> getFundingListFriend(@AuthenticationPrincipal Long userId, @PathVariable Long friendId){
        return ResponseEntity.ok().body(fundingService.getFundingListFriend(userId,friendId));
    }

    //주석 해제
    @GetMapping("/history/target/{friendId}")
    public ResponseEntity<List<FundingListFriendResponse>> getTargetFundingListFriend(@AuthenticationPrincipal Long userId, @PathVariable Long friendId){
        return ResponseEntity.ok().body(fundingService.getTargetFundingListFriend(userId,friendId));
    }

    @GetMapping("/gift")
    public ResponseEntity<List<FundingMyGiftListResponse>> getMyFundingGift(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(fundingService.getMyFundigGifyList(userId));
    }

    @GetMapping("/detail/{fundingId}")
    public ResponseEntity<FundingDetailResponse> getFundingDetail(@AuthenticationPrincipal Long userId,
        @PathVariable Long fundingId){
        return ResponseEntity.ok().body(fundingService.getFundingDetail(userId,fundingId));
    }

    @GetMapping("/main")
    public ResponseEntity<List<FundingDetailResponse>> getFundingMain(@AuthenticationPrincipal Long userId){
        if(userId == null){
            return ResponseEntity.ok().body(fundingService.getFundingMain());
        }
        return ResponseEntity.ok().body(fundingService.getFundingMain(userId));
    }

}
