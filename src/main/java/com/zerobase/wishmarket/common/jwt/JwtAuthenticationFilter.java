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

            if (StringUtils.hasText(token) && this.jwtProvider.isValidationToken(token)) {
                // Redis ??? ?????? Token ??? ???????????? ?????? ??????
                String isLogout = (String) redisTemplate.opsForValue().get(ACCESS_TOKEN_BLACK_LIST_PREFIX + token);
                System.out.println(isLogout);
                if (ObjectUtils.isEmpty(isLogout)) {
                    // ????????? ???????????? ?????????????????? ?????? ????????? ???????????????.
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    // SecurityContext ??? Authentication ????????? ???????????????.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info(String.format("[%s] -> %s", this.jwtProvider.getUserId(token), request.getRequestURI()));
                }else{
                    request.setAttribute("exception", NOT_VERIFICATION_LOGOUT.getMessage() );
                }
            }
            // ????????? ???????????? ???, request??? attribute??? ???????????? RestAuthenticationEntryPoint??? request??? ????????????.
        } catch (ExpiredJwtException e) { // "????????? JWT ???????????????."
            log.error("JWT token is expired: {}", e.getMessage());
            request.setAttribute("exception", EXPIRED_ACCESS_TOKEN.getMessage());
        } catch (SignatureException e) { // "????????? JWT ???????????????."
            log.error("Invalid JWT signature: {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_SIGNATURE.getMessage());
        } catch (SecurityException | MalformedJwtException e) { // "???????????? ?????? ????????? JWT ???????????????.
            log.info("Invalid JWT Token {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) { // "???????????? ?????? ???????????? ????????? JWT ???????????????."
            log.info("Unsupported JWT Token {}", e.getMessage());
            request.setAttribute("exception", WRONG_TYPE_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("Illegal Argument Exception {}", e.getMessage());
            request.setAttribute("exception", INVALID_TOKEN.getMessage());
        }

        filterChain.doFilter(request, response);

    }


    // request??? ?????? header????????? token ??????
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(ACCESS_TOKEN_PREFIX)) {
            return token.substring(ACCESS_TOKEN_PREFIX.length());
        }

        return null;
    }
}
