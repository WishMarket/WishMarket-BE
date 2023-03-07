package com.zerobase.wishmarket.domain.alarm.service;

import com.zerobase.wishmarket.domain.alarm.exception.AlarmErrorCode;
import com.zerobase.wishmarket.domain.alarm.exception.AlarmException;
import com.zerobase.wishmarket.domain.alarm.model.entity.Alarm;
import com.zerobase.wishmarket.domain.alarm.model.dto.AlarmResponseDto;
import com.zerobase.wishmarket.domain.alarm.model.type.AlarmMessage;
import com.zerobase.wishmarket.domain.alarm.repository.AlarmRepository;
import com.zerobase.wishmarket.domain.funding.model.entity.Funding;
import com.zerobase.wishmarket.domain.funding.model.entity.FundingParticipation;
import com.zerobase.wishmarket.domain.user.exception.UserErrorCode;
import com.zerobase.wishmarket.domain.user.exception.UserException;
import com.zerobase.wishmarket.domain.user.repository.UserAuthRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final AlarmRepository alarmRepository;
    private final UserAuthRepository userAuthRepository;

    //알람 생성 기본로직
    public void addAlarm(Long userId, String contents) {
        boolean exist = userAuthRepository.existsById(userId);
        if (!exist) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        Alarm alarm = Alarm.builder()
            .userId(userId)
            .contents(contents)
            .alarmStatus(false)
            .build();
        alarmRepository.save(alarm);
        sendUnreadCount(alarm.getUserId());
    }

    //내 알람
    public List<AlarmResponseDto> getMyAlarms(Long userId) {
        List<Alarm> alarmsList = alarmRepository.findAllByUserId(userId);

        return alarmsList.stream()
            .map(AlarmResponseDto::of)
            .collect(Collectors.toList());
    }

    //알람 읽음 처리
    @Transactional
    public void readAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
            .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

        alarm.setAsRead();
        alarmRepository.save(alarm);

        sendUnreadCount(alarm.getUserId());
    }

    //알람 삭제
    public void deleteAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
            .orElseThrow(() -> new AlarmException(AlarmErrorCode.ALARM_NOT_FOUND));

        alarmRepository.deleteById(alarmId);

        sendUnreadCount(alarm.getUserId());
    }

    //펀딩 관련 알람보내기
    public void addFundingAlarm(Funding funding) {
        Long targetUserId = funding.getTargetUser().getUserId();
        Long startUserId = funding.getUser().getUserId();
        List<FundingParticipation> participationList = funding.getParticipationList();

        switch (funding.getFundingStatusType()) {
            case SUCCESS:
                addAlarm(targetUserId, AlarmMessage.SUCCESS_TARGET.name());
                addAlarm(startUserId, AlarmMessage.SUCCESS_PARTICIPANT.name());
                if (participationList != null) {
                    for (FundingParticipation participation : participationList) {
                        addAlarm(participation.getUser().getUserId(),
                            AlarmMessage.SUCCESS_PARTICIPANT.name());
                    }
                }
                break;

            case FAIL:
                addAlarm(targetUserId, AlarmMessage.FAIL_FUNDING.name());
                addAlarm(startUserId, AlarmMessage.FAIL_FUNDING.name());
                if (participationList != null) {
                    for (FundingParticipation participation : participationList) {
                        addAlarm(participation.getUser().getUserId(),
                            AlarmMessage.FAIL_FUNDING.name());
                    }
                }
                break;
        }
    }

    //SseEmitter연결
    public SseEmitter addSseEmitter(Long userId) {
        SseEmitter sseEmitter = new SseEmitter();
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException exception) {
            throw new AlarmException(AlarmErrorCode.ALARM_CONNECTION_ERROR);
        }
        sseEmitters.put(userId, sseEmitter);
        sseEmitter.onCompletion(() -> sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(userId));
        sseEmitter.onError((e) -> sseEmitters.remove(userId));
        System.out.println("SseEmitter 생성 userId: " + userId);
        return sseEmitter;
    }

    public int countUnreadAlarms(Long userId) {
        return alarmRepository.countByUserIdAndAlarmStatusFalse(userId);
    }

    private void sendUnreadCount(Long userId) {
        if(sseEmitters.containsKey(userId)){
            SseEmitter sseEmitter = sseEmitters.get(userId);
            try{
                sseEmitter.send(SseEmitter.event().name("alarm").data(countUnreadAlarms(userId)));
            }catch (Exception e){
                sseEmitters.remove(userId);
            }
        }
    }
}
