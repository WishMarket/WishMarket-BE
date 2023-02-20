package com.zerobase.wishmarket.domain.alarm.service;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import com.zerobase.wishmarket.exception.CommonErrorCode;
import com.zerobase.wishmarket.exception.GlobalException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final UserAuthRepository userAuthRepository;

    public Alarm addAlarm(Long userId,String contents) {
        boolean exist = userAuthRepository.existsById(userId);
        if (!exist) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        Alarm alarm = Alarm.builder()
            .userId(userId)
            .contents(contents)
            .build();
        alarmRepository.save(alarm);

        return alarm;
    }

    public List<AlarmResponseDto> getMyAlarms(Long userId) {
        List<Alarm> alarmsList = alarmRepository.findAllByUserId(userId);
        if (alarmsList.isEmpty()) {
            throw new AlarmException(AlarmErrorCode.ALARM_IS_EMPTY);
        }
        List<AlarmResponseDto> responseAlarmDtoList = new ArrayList<>();
        for (Alarm alarm : alarmsList) {
            AlarmResponseDto responseDto = AlarmResponseDto.of(alarm);
            responseAlarmDtoList.add(responseDto);
        }
        return responseAlarmDtoList;
    }

    public AlarmResponseDto readAlarm(Long alarmId, Long userId) {
        Alarm alarm = alarmRepository.findById(alarmId)
            .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

        if (!alarm.getUserId().equals(userId)) {
            throw new GlobalException(CommonErrorCode.INVALID_TOKEN);
        }
        alarm.setAsRead();
        alarmRepository.save(alarm);

        return AlarmResponseDto.of(alarm);
    }

}
