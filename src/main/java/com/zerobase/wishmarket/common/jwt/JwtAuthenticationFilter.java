package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_BLACK_LIST_PREFIX;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_PREFIX;
import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.TOKEN_HEADER;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.INVALID_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.NOT_VERIFICATION_LOGOUT;
import static com.zerobase.wishmarket.exception.CommonErrorCode.WRONG_TYPE_SIGNATURE;
import static com.zerobase.wishmarket.exception.CommonErrorCode.WRONG_TYPE_TOKEN;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String token = this.resolveTokenFromRequest(request);
            log.info("전달되는 토큰 : " + token);
            if (StringUtils.hasText(token) && this.jwtProvider.isValidationToken(token)) {
                // Redis 에 해당 Token 의 로그아웃 여부 확인
                String isLogout = (String) redisTemplate.opsForValue().get(ACCESS_TOKEN_BLACK_LIST_PREFIX + token);
                System.out.println(isLogout);
                if (ObjectUtils.isEmpty(isLogout)) {
                    // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    // SecurityContext 에 Authentication 객체를 저장합니다.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info(String.format("[%s] -> %s", this.jwtProvider.getUserId(token), request.getRequestURI()));
                }else{
                    request.setAttribute("exception", NOT_VERIFICATION_LOGOUT.getMessage() );
                }
            }
            // 에러가 발생했을 때, request에 attribute를 세팅하고 RestAuthenticationEntryPoint로 request를 넘겨준다.
        } catch (ExpiredJwtException e) { // "만료된 JWT 토큰입니다."
            log.error("JWT token is expired: {}", e.getMessage());
            request.setAttribute("exception", EXPIRED_ACCESS_TOKEN.getMessage());
        } catch (SignatureException e) { // "잘못된 JWT 서명입니다."
            log.error("Invalid JWT signature: {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_SIGNATURE.getMessage());
        } catch (SecurityException | MalformedJwtException e) { // "유효하지 않은 구성의 JWT 토큰입니다.
            log.info("Invalid JWT Token {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) { // "지원되지 않는 형식이나 구성의 JWT 토큰입니다."
            log.info("Unsupported JWT Token {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("Illegal Argument Exception {}", e.getMessage());
            request.setAttribute("exception", INVALID_TOKEN.getMessage());
        }

        filterChain.doFilter(request, response);

    }


    // request에 있는 header로부터 token 얻기
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(ACCESS_TOKEN_PREFIX)) {
            return token.substring(ACCESS_TOKEN_PREFIX.length());
        }

        return null;
    }
}
