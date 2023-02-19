package com.zerobase.wishmarket.common.jwt.model.constants;

import java.time.Duration;

public interface JwtConstants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    long ACCESS_TOKEN_VALID_TIME = Duration.ofMinutes(60).toMillis(); // Access Token: 30분
    long REFRESH_TOKEN_VALID_TIME = Duration.ofDays(14).toMillis(); // 만료시간 2주
    String REFRESH_TOKEN_PREFIX = "Refresh:";
    String ACCESS_TOKEN_BLACK_LIST_PREFIX = "Black-Access:";
}
