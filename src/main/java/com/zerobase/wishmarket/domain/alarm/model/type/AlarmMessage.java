
package com.zerobase.wishmarket.domain.alarm.model.type;

public enum AlarmMessage {
    FUNDING_START_ALARM_FOR_TARGET("당신을 위한 펀딩이 시작되었습니다."),
    FUNDING_SUCCESS_ALARM_FOR_TARGET("선물받은 펀딩이 성공하였습니다."),
    FUNDING_FAIL_ALARM_FOR_TARGET("선물받은 펀딩이 실패하였습니다."),
    FUNDING_SUCCESS_ALARM_FOR_PARTICIPANT("참여하신 펀딩이 성공하였습니다."),
    FUNDING_FAIL_ALARM_FOR_PARTICIPANT("참여하신 펀딩이 실패하였습니다."),

    ;

    private final String message;

    AlarmMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
