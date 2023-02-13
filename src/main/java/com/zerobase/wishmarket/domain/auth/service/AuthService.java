package com.zerobase.wishmarket.domain.auth.service;

import static com.zerobase.wishmarket.domain.auth.exception.AuthErrorCode.INVALID_AUTH_CODE;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_KEY;

import com.zerobase.wishmarket.client.RedisClient;
import com.zerobase.wishmarket.domain.auth.components.MailComponents;
import com.zerobase.wishmarket.domain.auth.exception.AuthException;
import com.zerobase.wishmarket.domain.auth.model.dto.AuthCodeMailForm;
import com.zerobase.wishmarket.domain.auth.model.dto.AuthCodeVerifyForm;
import com.zerobase.wishmarket.exception.GlobalException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long REDIS_AUTH_CODE_EXPIRE_TIME = 1000 * 60 * 3; // 3분
    private static final int AUTH_CODE_LENGTH = 6;
    private static final String KEY_PREFIX = "auth_key:";

    private final MailComponents mailComponents;
    private final RedisClient redisClient;


    public void sendAuthCode(AuthCodeMailForm form) {
        String authCode = getRandomAuthCode();
        String key = KEY_PREFIX + form.getName() + form.getEmail();

        // client에서 인증 코드 만료 시간 3분 안에 페이지를 벗어났다가 인증 코드 재요청 시
        // 해당 key 삭제 후 메일 전송
        if (redisClient.hasKey(key)) {
            redisClient.del(key);
        }

        mailComponents.sendAuthCodeMail(form.getEmail(), authCode);

        // 메일 전송 성공 시 redis에 키 저장
        redisClient.put(key, authCode, TimeUnit.MILLISECONDS, REDIS_AUTH_CODE_EXPIRE_TIME);
    }

    public void authCodeVerify(AuthCodeVerifyForm form) {
        String key = KEY_PREFIX + form.getName() + form.getEmail();

        // key 시간 만료
        if (!redisClient.hasKey(key)) {
            throw new GlobalException(EXPIRED_KEY);
        }

        String verifiedAuthCode = redisClient.get(key, String.class);

        if(!verifiedAuthCode.equals(form.getCode())){
            throw new AuthException(INVALID_AUTH_CODE);
        }

        // 정상적인 인증 후 key 제거
        redisClient.del(key);
    }

    private String getRandomAuthCode() {
        return RandomStringUtils.random(AUTH_CODE_LENGTH, true, true);
    }
}
