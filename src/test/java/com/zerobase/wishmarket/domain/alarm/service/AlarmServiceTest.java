package com.zerobase.wishmarket.domain.alarm.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.entity.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.model.type.AlarmMessage;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.model.entity.UserEntity;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.time.LocalDateTime;
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
        Long alarmId = 1L;
        Alarm alarm = Alarm.builder()
            .id(alarmId)
            .userId(userId)
            .contents("testAlarm")
            .isRead(false)
            .build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));

        // when
       alarmService.readAlarm(alarmId);

        // then
        verify(alarmRepository, times(1)).findById(alarmId);
        verify(alarmRepository, times(1)).save(alarm);
        assertTrue(alarm.isRead());
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
        Long userId = 1L;
        Alarm alarm = Alarm.builder()
            .id(alarmId)
            .userId(userId)
            .contents("testAlarm")
            .isRead(false)
            .build();
        given(alarmRepository.findById(alarmId)).willReturn(Optional.of(alarm));

        // when
        alarmService.deleteAlarm(alarmId);

        // then
        verify(alarmRepository, times(1)).deleteById(alarmId);
    }

    @Test
    void addFundingAlarm_success() {
        // given
        UserEntity targetUser = UserEntity.builder().userId(1L).name("targetUser").build();
        UserEntity startUser = UserEntity.builder().userId(2L).name("startUser").build();
        UserEntity participant1 = UserEntity.builder().userId(3L).name("participant1").build();
        UserEntity participant2 = UserEntity.builder().userId(4L).name("participant2").build();

        Funding funding = Funding.builder()
            .id(1L)
            .targetUser(targetUser)
            .user(startUser)
            .fundingStatusType(FundingStatusType.SUCCESS)
            .participationList(new ArrayList<>())
            .build();
        FundingParticipation participation1 = FundingParticipation.builder()
            .id(3L)
            .funding(funding)
            .user(participant1)
            .price(1000L)
            .fundedAt(LocalDateTime.now())
            .build();
        FundingParticipation participation2 = FundingParticipation.builder()
            .id(4L)
            .funding(funding)
            .user(participant2)
            .price(2000L)
            .fundedAt(LocalDateTime.now())
            .build();
        funding.getParticipationList().add(participation1);
        funding.getParticipationList().add(participation2);

        given(userAuthRepository.existsById(targetUser.getUserId())).willReturn(true);
        given(userAuthRepository.existsById(startUser.getUserId())).willReturn(true);
        given(userAuthRepository.existsById(participation1.getUser().getUserId())).willReturn(true);
        given(userAuthRepository.existsById(participation2.getUser().getUserId())).willReturn(true);

        // when
        alarmService.addFundingAlarm(funding);

        // then
        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository, times(4)).save(alarmCaptor.capture());

        List<Alarm> alarms = alarmCaptor.getAllValues();
        assertThat(alarms.get(0).getUserId()).isEqualTo(targetUser.getUserId());
        assertThat(alarms.get(0).getContents()).isEqualTo(AlarmMessage.FUNDING_SUCCESS_ALARM_FOR_TARGET.getMessage());

        assertThat(alarms.get(1).getUserId()).isEqualTo(startUser.getUserId());
        assertThat(alarms.get(1).getContents()).isEqualTo(AlarmMessage.FUNDING_SUCCESS_ALARM_FOR_PARTICIPANT.getMessage());

        assertThat(alarms.get(2).getUserId()).isEqualTo(participant1.getUserId());
        assertThat(alarms.get(2).getContents()).isEqualTo(AlarmMessage.FUNDING_SUCCESS_ALARM_FOR_PARTICIPANT.getMessage());

        assertThat(alarms.get(3).getUserId()).isEqualTo(participant2.getUserId());
        assertThat(alarms.get(3).getContents()).isEqualTo(AlarmMessage.FUNDING_SUCCESS_ALARM_FOR_PARTICIPANT.getMessage());
    }

}