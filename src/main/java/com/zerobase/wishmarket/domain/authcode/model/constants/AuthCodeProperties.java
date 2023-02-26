package com.zerobase.wishmarket.domain.authcode.model.constants;

public interface AuthCodeProperties {

    long REDIS_AUTH_CODE_EXPIRE_TIME = 1000 * 60 * 5; // 5분
    int AUTH_CODE_LENGTH = 6;
    String KEY_PREFIX = "auth_key:";
    String AUTH_MAIL_SEND_SUCCESS = "인증코드가 발송되었습니다";
    String AUTH_CODE_VERIFICATION_SUCCESS = "인증이 완료되었습니다.";
}
