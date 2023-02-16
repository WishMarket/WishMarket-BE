package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.TOKEN_HEADER;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.TOKEN_PREFIX;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.INVALID_TOKEN;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String token = this.resolveTokenFromRequest(request);

            if (StringUtils.hasText(token) && this.jwtProvider.isValidationToken(token, request)) {
                // 토큰 유효성 검증
                Authentication auth = this.jwtProvider.getAuthentication(token);

                // Security Context 에 인증 정보 넣기
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.info(String.format("[%s] -> %s", this.jwtProvider.getUserId(token), request.getRequestURI()));
            }
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            request.setAttribute("exception", EXPIRED_ACCESS_TOKEN.getMessage());
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token {}", e.getMessage());
            request.setAttribute("exception", INVALID_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token {}", e.getMessage());
            request.setAttribute("exception", INVALID_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("Illegal Argument Exception {}", e.getMessage());
            request.setAttribute("exception", INVALID_TOKEN.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    // request에 있는 header로부터 token 얻기
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
