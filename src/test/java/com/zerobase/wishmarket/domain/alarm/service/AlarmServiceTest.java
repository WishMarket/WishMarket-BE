package com.zerobase.wishmarket.domain.alarm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        String contents = "Message!";
        given(userAuthRepository.existsById(userId)).willReturn(true);
        // when
        alarmService.addAlarm(userId, contents);
        ArgumentCaptor<Alarm> alarmArgumentCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository, times(1)).save(alarmArgumentCaptor.capture());

        // then
        Alarm alarm = alarmArgumentCaptor.getValue();
        assertEquals(userId, alarm.getUserId());
        assertEquals(contents, alarm.getContents());
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
    public void getMyAlarms() {
        Long userId = 10L;
        List<Alarm> alarms = Arrays.asList(
            Alarm.builder().id(1L).userId(userId).contents("test1").build(),
            Alarm.builder().id(2L).userId(userId).contents("test2").build()
        );
        given(alarmRepository.findAllByUserId(userId)).willReturn(alarms);

        List<AlarmResponseDto> results1 = alarmService.getMyAlarms(userId);

        assertEquals(2, results1.size());
        assertEquals("test1", results1.get(0).getContents());
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
        AlarmResponseDto readAlarm = alarmService.readAlarm(alarmId);

        // then
        assertTrue(readAlarm.isRead());
        assertEquals(alarm.getContents(), readAlarm.getContents());
    }

    @Test
    public void readAlarmTest_ALARM_NOT_FOUND() {
        //when
        AlarmException exception = assertThrows(AlarmException.class,
            () -> alarmService.readAlarm(1L));
        // then
        assertEquals(AlarmErrorCode.ALARM_NOT_FOUND, exception.getErrorCode());

    }


    @Test
    public void deleteAlarmTest() {
        // given
        Long alarmId = 1L;
        given(alarmRepository.existsById(alarmId)).willReturn(true);

        // when
        alarmService.deleteAlarm(alarmId);

        // then
        verify(alarmRepository, times(1)).deleteById(alarmId);
    }

}