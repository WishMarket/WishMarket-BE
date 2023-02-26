package com.zerobase.wishmarket.domain.alarm.controller;

import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.service.AlarmService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/alarms")
@RestController
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/")
    public ResponseEntity<List<AlarmResponseDto>> myAlarms(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok().body(alarmService.getMyAlarms(userId));
    }

    @PutMapping("/{alarmId}/read")
    public ResponseEntity<AlarmResponseDto> setAsRead(@PathVariable Long alarmId) {
        return ResponseEntity.ok().body(alarmService.readAlarm(alarmId));
    }

    @DeleteMapping("/{alarmId}/delete")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);
        return ResponseEntity.noContent().build();
    }

}
