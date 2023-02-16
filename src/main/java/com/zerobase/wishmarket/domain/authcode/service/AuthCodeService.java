package com.zerobase.wishmarket.domain.authcode.service;

import static com.zerobase.wishmarket.domain.authcode.exception.AuthErrorCode.INVALID_AUTH_CODE;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.AUTH_CODE_LENGTH;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.KEY_PREFIX;
import static com.zerobase.wishmarket.domain.authcode.model.constants.AuthCodeProperties.REDIS_AUTH_CODE_EXPIRE_TIME;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_KEY;

import com.zerobase.wishmarket.common.redis.RedisClient;
import com.zerobase.wishmarket.domain.authcode.components.MailComponents;
import com.zerobase.wishmarket.domain.authcode.exception.AuthException;
import com.zerobase.wishmarket.domain.authcode.model.dto.AuthCodeMailForm;
import com.zerobase.wishmarket.domain.authcode.model.dto.AuthCodeVerifyForm;
import com.zerobase.wishmarket.exception.GlobalException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCodeService {



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
