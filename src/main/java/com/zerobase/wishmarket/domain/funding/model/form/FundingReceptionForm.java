package com.zerobase.wishmarket.domain.funding.model.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundingReceptionForm {

    private Long fundingId;
    private Long productId;
    private String address;
    private String detailAddress;
    private String comment;
    private Boolean isLike;
}
