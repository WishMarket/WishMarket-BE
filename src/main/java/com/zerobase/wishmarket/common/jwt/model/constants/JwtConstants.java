package com.zerobase.wishmarket.common.jwt.model.constants;

import java.time.Duration;

public interface JwtConstants {

    String TOKEN_HEADER = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
    long ACCESS_TOKEN_VALID_TIME = Duration.ofHours(12).toMillis(); // Access Token: 12시간
    long REFRESH_TOKEN_VALID_TIME = Duration.ofDays(30).toMillis(); // 만료 기간 30일
    long ACCESS_REFRESH_TOKEN_REISSUE_TIME = Duration.ofDays(15).toMillis(); // 15일
    String REFRESH_TOKEN_PREFIX = "Refresh:";
    String ACCESS_TOKEN_BLACK_LIST_PREFIX = "Black-Access:";
}
