package com.zerobase.wishmarket.client;

import static com.zerobase.wishmarket.exception.CommonErrorCode.REDIS_PUT_EMPTY_KEY;
import static com.zerobase.wishmarket.exception.CommonErrorCode.REDIS_PUT_FAIL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public <T> T get(String key, Class<T> classType) {
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        }

        String redisValue = (String)redisTemplate.opsForValue().get(key);

        if (StringUtil.isNullOrEmpty(redisValue)) {
            return null;
        } else {
            try {
                return objectMapper.readValue(redisValue, classType);
            } catch (JsonProcessingException ex) {
                log.error("Parsing error", ex);
                return null;
            }
        }
    }

    public void put(String key, Object classType) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw new GlobalException(REDIS_PUT_EMPTY_KEY);
        }

        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(classType));
        } catch (JsonProcessingException ex) {
            throw new GlobalException(REDIS_PUT_FAIL);
        }
    }

    public void put(String key, Object classType, TimeUnit expireTimeUnit, Long expireTime) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw new GlobalException(REDIS_PUT_EMPTY_KEY);
        }

        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(classType));
            redisTemplate.expire(key, expireTime, expireTimeUnit);
        } catch (JsonProcessingException ex) {
            throw new GlobalException(REDIS_PUT_FAIL);
        }
    }

    public void del(String key) {
        if (!StringUtil.isNullOrEmpty(key)) {
            redisTemplate.delete(key);
        }
    }
}
