package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_VALID_TIME;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_VALID_TIME;

import com.zerobase.wishmarket.common.jwt.model.dto.TokenSetDto;
import com.zerobase.wishmarket.common.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final UserDetailsService userDetailsService;

    // JWT AccessToken 생성
    public String generateAccessToken(Long id) {
        Date now = new Date();

        return Jwts.builder()
            .setSubject(Aes256Util.encrypt(String.valueOf(id)))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }

    // JWT RefreshToken 생성
    public String generateRefreshToken(Long id) {
        Date now = new Date();

        return Jwts.builder()
            .setSubject(Aes256Util.encrypt(String.valueOf(id)))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }


    // JWT accessToken, refreshToken 생성
    public TokenSetDto generateTokenSet(Long id) {
        Date now = new Date();

        String accessToken = Jwts.builder()
            .setSubject(Aes256Util.encrypt(String.valueOf(id)))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();

        String refreshToken = Jwts.builder()
            .setSubject(Aes256Util.encrypt(String.valueOf(id)))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();

        return TokenSetDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public boolean isValidationToken(String token) {
        Claims claims = parseClaims(token);

        // 만료 시간이 현재 시간보다 적을 때만 유효함.
        return !claims.getExpiration().before(new Date());
    }

    // Jwt 토큰에서 회원 Id 얻기
    public String getUserId(String accessToken) {
        Claims claims = this.parseClaims(accessToken);

        return Aes256Util.decrypt(claims.getSubject());
    }

    // Jwt 토큰 만료 기간 얻기
    public Date getExpiredDate(String token) {
        Claims claims = this.parseClaims(token);

        return claims.getExpiration();
    }

    // jwt토큰으로부터 인증 정보 가져오기
    public Authentication getAuthentication(String jwt) {
        // memberservice의 loadUserByUsername로부터 회원 가입 정보 가져오기
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getUserId(jwt));

        // Spring에서 지원해주는 형태의 토큰으로 바꿔주기
        // userDetails와 권한 정보 넣어주기
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
