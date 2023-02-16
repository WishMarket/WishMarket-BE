package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.exception.CommonErrorCode.ACCESS_DENIED;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.INVALID_TOKEN;

import com.zerobase.wishmarket.exception.ErrorCode;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증이 되지 않은 사용자가 요청을 한 경우 동작하기 위해 AuthenticationEntryPoint를 상속받는 핸들러를 구현. 에러코드를 반환하는 역할을 한다.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        String exception = (String) request.getAttribute("exception");

        if (exception == null) {
            log.error("JwtAuthenticationEntryPoint : " + ACCESS_DENIED);
            setResponse(response, ACCESS_DENIED);
        } else if (exception.equals(EXPIRED_ACCESS_TOKEN.getMessage())) {
            log.error("JwtAuthenticationEntryPoint : " + EXPIRED_ACCESS_TOKEN);
            setResponse(response, EXPIRED_ACCESS_TOKEN);
        } else if (exception.equals(INVALID_TOKEN.getMessage())) {
            log.error("JwtAuthenticationEntryPoint : " + INVALID_TOKEN);
            setResponse(response, INVALID_TOKEN);
        } else {
            log.error("JwtAuthenticationEntryPoint : " + ACCESS_DENIED);
            setResponse(response, ACCESS_DENIED);
        }
    }

    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", errorCode.getMessage());
        responseJson.put("code", errorCode.getErrorCode());

        response.getWriter().print(responseJson);
    }
}
