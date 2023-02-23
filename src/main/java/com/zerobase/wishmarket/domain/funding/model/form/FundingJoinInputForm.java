package com.zerobase.wishmarket.domain.funding.model.form;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundingJoinInputForm {

    private Long fundingId;
    private Long fundedPrice;
    private LocalDateTime fundedAt;

}
