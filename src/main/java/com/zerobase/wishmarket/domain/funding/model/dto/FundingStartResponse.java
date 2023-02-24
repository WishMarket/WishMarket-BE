package com.zerobase.wishmarket.domain.funding.model.dto;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.type.FundedStatusType;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FundingStartResponse {

    private Long fundingId;
    private Long userId;
    private Long targetId;
    private Long productId;
    private Long targetPrice;
    private Long fundedPrice;
    private Long participationCount;
    private FundingStatusType fundingStatusType;
    private FundedStatusType fundedStatusType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static FundingStartResponse of(Funding funding) {
        return FundingStartResponse.builder()
            .fundingId(funding.getId())
            .userId(funding.getUser().getUserId())
            .targetId(funding.getTargetUser().getUserId())
            .productId(funding.getProduct().getProductId())
            .targetPrice(funding.getTargetPrice())
            .fundedPrice(funding.getFundedPrice())
            .participationCount(1L)
            .fundingStatusType(funding.getFundingStatusType())
            .fundedStatusType(funding.getFundedStatusType())
            .startDate(funding.getStartDate())
            .endDate(funding.getEndDate())
            .build();

    }

}
