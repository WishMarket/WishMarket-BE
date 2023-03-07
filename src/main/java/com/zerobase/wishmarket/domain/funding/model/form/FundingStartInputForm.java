package com.zerobase.wishmarket.domain.funding.model.form;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public LocalDateTime getStartZoneDate() {
        return ZonedDateTime.of(startDate, ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
    public LocalDateTime getEndZoneDate() {
        return ZonedDateTime.of(endDate,ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }


}
