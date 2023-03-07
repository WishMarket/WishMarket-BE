package com.zerobase.wishmarket.domain.alarm.model.dto;

import com.zerobase.wishmarket.domain.alarm.model.entity.Alarm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmResponseDto {
    Long id;
    String contents;
    boolean isRead;

    public static AlarmResponseDto of(Alarm alarm) {
        return AlarmResponseDto.builder()
            .id(alarm.getId())
            .contents(alarm.getContents())
            .isRead(alarm.isAlarmStatus())
            .build();
    }
}
