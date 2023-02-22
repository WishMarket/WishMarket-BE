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
public class FundingStartDto {

    private Long fundingId;
    private Long userId;
    private Long targetId;
    private Long targetPrice;
    private Long fundedPrice;
    private FundingStatusType fundingStatusType;
    private FundedStatusType fundedStatusType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static FundingStartDto of(Funding funding) {
        return FundingStartDto.builder()
            .fundingId(funding.getId())
            .userId(funding.getUser().getUserId())
            .targetId(funding.getTargetUser().getUserId())
            .targetPrice(funding.getTargetPrice())
            .fundedPrice(funding.getFundedPrice())
            .fundingStatusType(funding.getFundingStatusType())
            .fundedStatusType(funding.getFundedStatusType())
            .startDate(funding.getStartDate())
            .endDate(funding.getEndDate())
            .build();

    }

}
