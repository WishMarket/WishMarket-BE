package com.zerobase.wishmarket.common.jwt.model.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenSetDto {

    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpiredAt;
    private Date refreshTokenExpiredAt;
}
