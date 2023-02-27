package com.zerobase.wishmarket.domain.funding.model.dto;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FundingMyGiftListResponse {

    private Long fundingId;

    private Long productId;
    private String productName;
    private String productImagerUrl;
    private Long price;

    private Long fundedPrice; // 모인 금액
    private List<String> participants;
    private Long participantsNumber;

    private FundedStatusType fundedStatusType;
    private String review;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static FundingMyGiftListResponse from(Funding funding, String review, List<String> participants){
        return FundingMyGiftListResponse.builder()
            .fundingId(funding.getId())
            .productId(funding.getProduct().getProductId())
            .productName(funding.getProduct().getName())
            .productImagerUrl(funding.getProduct().getProductImage())
            .price(funding.getTargetPrice())
            .participants(participants)
            .participantsNumber(funding.getParticipationCount())
            .fundedPrice(funding.getFundedPrice())
            .fundedStatusType(funding.getFundedStatusType())
            .review(review)
            .startDate(funding.getStartDate())
            .endDate(funding.getEndDate())
            .build();
    }
}
