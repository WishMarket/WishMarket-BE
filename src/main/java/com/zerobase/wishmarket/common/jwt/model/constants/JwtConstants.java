package com.zerobase.wishmarket.common.jwt.model.constants;

import java.time.Duration;

public interface JwtConstants {

    String TOKEN_HEADER = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
    long ACCESS_TOKEN_VALID_TIME = Duration.ofHours(3).toMillis(); // Access Token: 3시간
    long REFRESH_TOKEN_VALID_TIME = Duration.ofDays(1).toMillis(); // 만료 기간 1일
    long ACCESS_REFRESH_TOKEN_REISSUE_TIME = Duration.ofHours(12).toMillis(); // 12시간
    String REFRESH_TOKEN_PREFIX = "Refresh:";
    String ACCESS_TOKEN_BLACK_LIST_PREFIX = "Black-Access:";
}
