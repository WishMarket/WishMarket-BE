package com.zerobase.wishmarket.domain.funding.model.dto;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FundingDetailResponse {

    private Long fundingId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long targetUserId;
    private String targetUserName;
    private String targetUserProfileImageUrl;
    private Long targetPrice;
    private Long myFundedPrice;
    private Long totalFundedPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private FundingStatusType fundingStatusType;
    private FundedStatusType fundedStatusType;
    private Long participationCount;
    private List<String> participantsNameList;

    public static FundingDetailResponse from(Funding funding, List<String> participantsNameList, Long userFundedPrice) {
        return FundingDetailResponse.builder()
            .fundingId(funding.getId())
            .productId(funding.getProduct().getProductId())
            .productName(funding.getProduct().getName())
            .productImageUrl(funding.getProduct().getProductImage())
            .targetUserId(funding.getTargetUser().getUserId())
            .targetUserName(funding.getTargetUser().getName())
            .targetUserProfileImageUrl(funding.getTargetUser().getProfileImage())
            .targetPrice(funding.getTargetPrice())
            .myFundedPrice(userFundedPrice)      //조회한 유저가 해당 펀딩에 펀딩을 했다면, 금액이 보여야 함
            .totalFundedPrice(funding.getFundedPrice())
            .startDate(funding.getStartDate())
            .endDate(funding.getEndDate())
            .fundingStatusType(funding.getFundingStatusType())
            .fundedStatusType(funding.getFundedStatusType())
            .participationCount(funding.getParticipationCount())
            .participantsNameList(participantsNameList)
            .build();
    }

}
