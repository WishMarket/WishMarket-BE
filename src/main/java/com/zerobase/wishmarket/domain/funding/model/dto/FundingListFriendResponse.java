package com.zerobase.wishmarket.domain.funding.model.dto;

import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FundingListFriendResponse {

    private Long fundingId;
    private Long targetId;
    private String targetName;

    private Long productId;
    private String productName;
    private String productImagerUrl;
    private Long price;

    private Long fundedPrice;
    private Long myFundingPrice;
    private List<String> participants;
    private Long participantsNumber;

    private FundingStatusType fundStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static FundingListFriendResponse from(FundingParticipation participation, Funding funding,
        Long myFundingPrice, List<String> participantsNameList) {

        return FundingListFriendResponse.builder()
            .fundingId(funding.getId())
            .targetId(participation.getId())
            .targetName(funding.getTargetUser().getName())
            .productId(funding.getProduct().getProductId())
            .productName(funding.getProduct().getName())
            .productImagerUrl(funding.getProduct().getProductImage())
            .price(funding.getProduct().getPrice())
            .fundedPrice(funding.getFundedPrice())
            .myFundingPrice(myFundingPrice)
            .participants(participantsNameList)
            .participantsNumber(funding.getParticipationCount())
            .fundStatus(funding.getFundingStatusType())
            .startDate(funding.getStartDate())
            .endDate(funding.getEndDate())
            .build();

    }


}
