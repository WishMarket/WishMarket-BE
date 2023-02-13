package com.zerobase.wishmarket.common.jwt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenSetDto {
    private String accessToken;
    private String refreshToken;
}
