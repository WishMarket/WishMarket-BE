package com.zerobase.wishmarket.domain.authcode.model.constants;

public interface AuthCodeProperties {
    long REDIS_AUTH_CODE_EXPIRE_TIME = 1000 * 60 * 3; // 3분
    int AUTH_CODE_LENGTH = 6;
    String KEY_PREFIX = "auth_key:";
}
