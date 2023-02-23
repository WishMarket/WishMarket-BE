package com.zerobase.wishmarket.common.redis;

import static com.zerobase.wishmarket.exception.CommonErrorCode.REDIS_PUT_EMPTY_KEY;
import static com.zerobase.wishmarket.exception.CommonErrorCode.REDIS_PUT_FAIL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.wishmarket.common.jwt.JwtAuthenticationProvider;
import com.zerobase.wishmarket.exception.GlobalException;
import io.netty.util.internal.StringUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final JwtAuthenticationProvider provider;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String getAutoCode(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        }

        String redisValue = (String) redisTemplate.opsForValue().get(key);

        if (StringUtil.isNullOrEmpty(redisValue)) {
            return null;
        }

        return redisValue;
    }

    public String getRefreshToken(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        }

        String redisValue = (String) redisTemplate.opsForValue().get(key);

        if (StringUtil.isNullOrEmpty(redisValue)) {
            return null;
        }

        return redisValue;
    }

    public void put(String key, String value) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw new GlobalException(REDIS_PUT_EMPTY_KEY);
        }

        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new GlobalException(REDIS_PUT_FAIL);
        }
    }

    public void put(String key, String value, TimeUnit expireTimeUnit, Long expireTime) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw new GlobalException(REDIS_PUT_EMPTY_KEY);
        }

        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, expireTime, expireTimeUnit);
        } catch (Exception e) {
            throw new GlobalException(REDIS_PUT_FAIL);
        }
    }

    public void putAuthCode(String key, String authCode, TimeUnit expireTimeUnit, Long expireTime) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw new GlobalException(REDIS_PUT_EMPTY_KEY);
        }

        try {
            redisTemplate.opsForValue().set(key, authCode);
            redisTemplate.expire(key, expireTime, expireTimeUnit);
        } catch (Exception e) {
            throw new GlobalException(REDIS_PUT_FAIL);
        }
    }

    public boolean validationRefreshToken(String key, String refreshToken) {
        String redisRefreshToken = redisTemplate.opsForValue().get(key);
        System.out.println(redisRefreshToken);
        return refreshToken.equals(redisRefreshToken);
    }

    public void del(String key) {
        if (!StringUtil.isNullOrEmpty(key)) {
            redisTemplate.delete(key);
        }
    }
}
