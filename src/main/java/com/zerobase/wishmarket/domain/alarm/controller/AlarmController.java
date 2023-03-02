package com.zerobase.wishmarket.domain.alarm.controller;

import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.service.AlarmService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/api/alarms")
@RestController
public class AlarmController {

    private final AlarmService alarmService;

    //내 알람 검색
    @GetMapping("/")
    public ResponseEntity<List<AlarmResponseDto>> myAlarms(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok().body(alarmService.getMyAlarms(userId));
    }

    @PatchMapping("/{alarmId}/read")
    public ResponseEntity<Void> setAsRead(@PathVariable Long alarmId) {
        alarmService.readAlarm(alarmId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{alarmId}/delete")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);
        return ResponseEntity.noContent().build();
    }
    //SSE연결
    @GetMapping(value = "/sse", consumes = MediaType.ALL_VALUE)
    public SseEmitter sseEmitter(@AuthenticationPrincipal Long userId) {
        System.out.println("SseEmitter is requested for userId: " + userId);
        return alarmService.addSseEmitter(userId);
    }

    //BADGE 알람개수 요청
    @GetMapping("/badge")
    public ResponseEntity<Integer> myAlarmBadge(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok().body(alarmService.countUnreadAlarms(userId));
    }
}