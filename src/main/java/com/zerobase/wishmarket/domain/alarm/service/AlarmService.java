package com.zerobase.wishmarket.domain.alarm.service;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final UserAuthRepository userAuthRepository;

    public void addAlarm(Long userId, String contents) {
        boolean exist = userAuthRepository.existsById(userId);
        if (!exist) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        Alarm alarm = Alarm.builder()
            .userId(userId)
            .contents(contents)
            .build();
        alarmRepository.save(alarm);
    }

    @Cacheable(value = "alarms", key = "'user_' + #userId")
    public List<AlarmResponseDto> getMyAlarms(Long userId) {
        List<Alarm> alarmsList = alarmRepository.findAllByUserId(userId);
        if (alarmsList.isEmpty()) {
            throw new AlarmException(AlarmErrorCode.ALARM_IS_EMPTY);
        }

        return alarmsList.stream()
            .map(AlarmResponseDto::of)
            .collect(Collectors.toList());
    }

    public AlarmResponseDto readAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
            .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

        alarm.setAsRead();
        alarmRepository.save(alarm);

        return AlarmResponseDto.of(alarm);
    }

    public void deleteAlarm(Long alarmId) {
        boolean result = alarmRepository.existsById(alarmId);
        if (!result) {
            throw new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND);
        }
        alarmRepository.deleteById(alarmId);
    }

}
