package com.zerobase.wishmarket.domain.alarm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import com.zerobase.wishmarket.exception.CommonErrorCode;
import com.zerobase.wishmarket.exception.GlobalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private UserAuthRepository userAuthRepository;

    @InjectMocks
    private AlarmService alarmService;

    @Test
    public void addAlarmTest_success() {
        // given
        Long userId = 1L;
        UserEntity.builder().userId(userId).build();
        given(userAuthRepository.existsById(userId)).willReturn(true);
        String contents = "Message!";

        // when
        Alarm alarm = alarmService.addAlarm(userId, contents);
        System.out.println(alarm.toString());

        // then
        assertFalse(alarm.isRead());
        assertEquals(contents, alarm.getContents());
        assertEquals(userId, alarm.getUserId());
    }

    @Test
    public void AlarmTest_USER_NOT_FOUND_EXCEPTION() {
        //when
        UserException exception = assertThrows(UserException.class,
            () -> alarmService.addAlarm(1L, "Message!"));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void getMyAlarmsTest() {
        //given
        Long userId = 1L;
        UserEntity.builder().userId(userId).build();
        List<Alarm> alarmList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Alarm alarm = Alarm.builder()
                .id((long) (i + 1))
                .userId(userId)
                .build();
            alarmList.add(alarm);
        }
        given(alarmRepository.findAllByUserId(userId)).willReturn(alarmList);

        //when
        List<AlarmResponseDto> alarmDtoList = alarmService.getMyAlarms(userId);

        //then
        assertEquals(3, alarmDtoList.size());
        assertEquals(alarmList.size(), alarmDtoList.size());

    }

    @Test
    public void getMyAlarmsTest_emptyAlarm() {
        // given
        Long userId = 1L;
        UserEntity.builder().userId(userId).build();
        given(alarmRepository.findAllByUserId(userId)).willReturn(new ArrayList<>());

        // when
        AlarmException exception = assertThrows(AlarmException.class,
            () -> alarmService.getMyAlarms(userId));

        // then
        assertEquals(AlarmErrorCode.ALARM_IS_EMPTY, exception.getErrorCode());
    }

    @Test
    public void readAlarmTest_success() {
        //given
        Long userId = 1L;
        UserEntity.builder()
            .userId(userId)
            .build();

        Long alarmId = 1L;
        Alarm alarm = Alarm.builder()
            .id(alarmId)
            .userId(userId)
            .contents("testAlarm")
            .isRead(false)
            .build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));

        // when
        AlarmResponseDto readAlarm = alarmService.readAlarm(alarmId, userId);

        // then
        assertTrue(readAlarm.isRead());
        assertEquals(alarmId, readAlarm.getId());
        assertEquals(alarm.getContents(),readAlarm.getContents());
    }

    @Test
    public void readAlarmTest_ALARM_NOT_FOUND() {
        //when
        AlarmException exception = assertThrows(AlarmException.class,
            () -> alarmService.readAlarm(1L, 1L));
        // then
        assertEquals(AlarmErrorCode.ALARM_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    public void readAlarmTest_INVAID_TOKEN(){
        //given
        Long userId = 1L;
        UserEntity.builder()
            .userId(userId)
            .build();
        Long alarmId = 1L;
        Alarm alarm = Alarm.builder()
            .id(alarmId)
            .userId(userId)
            .contents("testAlarm")
            .isRead(false)
            .build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));

        //when
        GlobalException exception = assertThrows(GlobalException.class,
            () -> alarmService.readAlarm(1L, 2L));

        // then
        assertEquals(CommonErrorCode.INVALID_TOKEN, exception.getErrorCode());

    }

}