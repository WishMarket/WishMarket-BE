package com.zerobase.wishmarket.common.jwt.model.constants;

public interface JwtConstants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60; // Access Token: 1시간
    long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 15; // Refresh Token: 15일
}
