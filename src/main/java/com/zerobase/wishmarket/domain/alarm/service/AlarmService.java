package com.zerobase.wishmarket.domain.alarm.service;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.funding.model.type.FundingStatusType;
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
            .isRead(false)
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

    public void addFundingAlarm(Funding funding) {
        Long targetUserId = funding.getTargetUser().getUserId();
        Long startUserId = funding.getUser().getUserId();
        List<FundingParticipation> participationList = funding.getParticipationList();

        if (funding.getFundingStatusType() == FundingStatusType.SUCCESS) {
            addAlarm(targetUserId, "선물받은 펀딩이 성공하였습니다.");
            addAlarm(startUserId, "참여하신 펀딩이 성공하였습니다.");
            if (participationList != null) {
                for (FundingParticipation participation : participationList) {
                    Long participantId = participation.getUser().getUserId();
                    addAlarm(participantId, "참여하신 펀딩이 성공하였습니다.");
                }
            }
        } else if (funding.getFundingStatusType() == FundingStatusType.FAIL) {
            addAlarm(targetUserId, "선물받은 펀딩이 실패하였습니다.");
            addAlarm(startUserId, "참여하신 펀딩이 실패하였습니다.");
            if (participationList != null) {
                for (FundingParticipation participation : participationList) {
                    Long participantId = participation.getUser().getUserId();
                    addAlarm(participantId, "참여하신 펀딩이 실패하였습니다.");
                }
            }
        }
    }
}
