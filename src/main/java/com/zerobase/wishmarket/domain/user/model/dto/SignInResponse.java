package com.zerobase.wishmarket.domain.user.model.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse {
    private String email;
    private String name;
    private String accessToken;
    private String accessTokenExpiredAt;
    private String refreshToken;
    private String refreshTokenExpiredAt;
}
