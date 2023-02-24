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
public class FundingStartInputForm {

    private Long productId;
    private Long targetId;
    private Long fundedPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
