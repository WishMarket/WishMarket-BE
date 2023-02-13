package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.common.jwt.model.constants.JwtConstants.TOKEN_HEADER;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.INVALID_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.UNAUTHORIZED;

import com.zerobase.wishmarket.exception.ErrorCode;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증이 되지 않은 사용자가 요청을 한 경우 동작하기 위해 AuthenticationEntryPoint를 상속받는 핸들러를 구현. 에러코드를 반환하는 역할을 한다.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        String exception = (String) request.getAttribute(TOKEN_HEADER);

        if (exception.equals(EXPIRED_ACCESS_TOKEN.getMessage())) {
            setResponse(response, EXPIRED_ACCESS_TOKEN);
        } else if (exception.equals(INVALID_TOKEN.getMessage())) {
            setResponse(response, INVALID_TOKEN);
        } else {
            setResponse(response, UNAUTHORIZED);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(errorCode);
    }
}
