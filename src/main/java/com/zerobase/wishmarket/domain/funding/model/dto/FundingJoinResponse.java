package com.zerobase.wishmarket.domain.funding.model.dto;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FundingJoinResponse {

    private Long fundingId;
    private Long productId;
    private Long targetId;
    private Long targetPrice;
    private Long fundedPrice;
    private Long participationCount;
    private Long totalFundedPrice;
    private FundingStatusType fundingStatusType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static FundingJoinResponse of(Funding funding) {
        return FundingJoinResponse.builder()
            .fundingId(funding.getId())
            .productId(funding.getProduct().getProductId())
            .targetId(funding.getTargetUser().getUserId())
            .targetPrice(funding.getTargetPrice())
            .fundedPrice(funding.getFundedPrice())
            .totalFundedPrice(funding.getFundedPrice())
            .participationCount(funding.getParticipationCount())
            .fundingStatusType(funding.getFundingStatusType())
            .startDate(funding.getStartDate())
            .endDate(funding.getEndDate())
            .build();
    }

}
