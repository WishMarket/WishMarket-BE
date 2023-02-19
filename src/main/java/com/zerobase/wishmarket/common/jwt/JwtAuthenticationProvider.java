package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_VALID_TIME;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.REFRESH_TOKEN_VALID_TIME;

import com.zerobase.wishmarket.common.jwt.model.dto.TokenSetDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
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

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT AccessToken 생성
    public TokenSetDto generateAccessToken(Long id) {
        Date now = new Date();

        Date accessTokenExpiredAt = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME);
        String accessToken = Jwts.builder()
            .setSubject(String.valueOf(id))
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiredAt)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();

        return TokenSetDto.builder()
            .accessToken(accessToken)
            .accessTokenExpiredAt(accessTokenExpiredAt)
            .build();
    }

    // JWT RefreshToken 생성
    public TokenSetDto generateRefreshToken(Long id) {
        Date now = new Date();

        Date refreshTokenExpiredAt = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);
        String refreshToken = Jwts.builder()
            .setSubject(String.valueOf(id))
            .setIssuedAt(now)
            .setExpiration(refreshTokenExpiredAt)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
        return TokenSetDto.builder()
            .refreshToken(refreshToken)
            .refreshTokenExpiredAt(refreshTokenExpiredAt)
            .build();
    }


    // JWT accessToken, refreshToken 생성
    public TokenSetDto generateTokenSet(Long id) {
        Date now = new Date();

        log.info("현재 시간 : " + now);
        log.info("Access Token 만료 시간 : " + new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME));
        Date accessTokenExpiredAt = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME);
        String accessToken = Jwts.builder()
            .setSubject(String.valueOf(id))
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiredAt)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();

        Date refreshTokenExpiredAt = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);
        String refreshToken = Jwts.builder()
            .setSubject(String.valueOf(id))
            .setIssuedAt(now)
            .setExpiration(refreshTokenExpiredAt)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();

        return TokenSetDto.builder()
            .accessToken(accessToken)
            .accessTokenExpiredAt(accessTokenExpiredAt)
            .refreshToken(refreshToken)
            .refreshTokenExpiredAt(refreshTokenExpiredAt)
            .build();
    }

    public boolean isValidationToken(String token, HttpServletRequest request) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch(SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature");
            return false;
        } catch(UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
            return false;
        } catch(IllegalArgumentException e) {
            log.error("JWT token is invalid");
            return false;
        }
    }

    // Jwt 토큰에서 회원 Id 얻기
    public String getUserId(String accessToken) {
        Claims claims = this.parseClaims(accessToken);

        return claims.getSubject();
    }

    // Jwt 토큰 만료 기간 얻기
    public Date getExpiredDate(String token) {
        return this.parseClaims(token).getExpiration();
    }


    // jwt토큰으로부터 인증 정보 가져오기
    public Authentication getAuthentication(String jwt) {
        // memberservice의 loadUserByUsername로부터 회원 가입 정보 가져오기
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(this.getUserId(jwt));

        // Spring에서 지원해주는 형태의 토큰으로 바꿔주기
        // userDetails와 권한 정보 넣어주기
        return new UsernamePasswordAuthenticationToken(Long.parseLong(this.getUserId(jwt)), "", userDetails.getAuthorities());
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
