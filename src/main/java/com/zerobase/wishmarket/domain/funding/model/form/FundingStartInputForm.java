package com.zerobase.wishmarket.domain.funding.model.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundingStartInputForm {

    private Long productId;
    private Long targetId;
    private Long fundedPrice;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+9")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+9")
    private LocalDateTime endDate;

    public LocalDateTime getStartZoneDate() {
        return ZonedDateTime.of(startDate, ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
    public LocalDateTime getEndZoneDate() {
        return ZonedDateTime.of(endDate,ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }


}
