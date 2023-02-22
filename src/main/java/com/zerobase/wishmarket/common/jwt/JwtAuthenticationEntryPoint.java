package com.zerobase.wishmarket.common.jwt;

import static com.zerobase.wishmarket.exception.CommonErrorCode.CREDENTIALS_DO_NOT_EXIST;
import static com.zerobase.wishmarket.exception.CommonErrorCode.EXPIRED_ACCESS_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.INVALID_TOKEN;
import static com.zerobase.wishmarket.exception.CommonErrorCode.WRONG_TYPE_SIGNATURE;
import static com.zerobase.wishmarket.exception.CommonErrorCode.WRONG_TYPE_TOKEN;

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
            log.info("JwtAuthenticationEntryPoint : " + CREDENTIALS_DO_NOT_EXIST);
            setResponse(response, CREDENTIALS_DO_NOT_EXIST);
        } else if (exception.equals(EXPIRED_ACCESS_TOKEN.getMessage())) { // 만료된 JWT 토큰입니다."
            log.info("JwtAuthenticationEntryPoint : " + EXPIRED_ACCESS_TOKEN);
            setResponse(response, EXPIRED_ACCESS_TOKEN);
        } else if (exception.equals(INVALID_TOKEN.getMessage())) {
            log.info("JwtAuthenticationEntryPoint : " + INVALID_TOKEN);
            setResponse(response, INVALID_TOKEN);
        } else if (exception.equals(WRONG_TYPE_SIGNATURE.getMessage())) { // "잘못된 JWT 서명입니다."
            log.info("JwtAuthenticationEntryPoint : " + WRONG_TYPE_SIGNATURE);
            setResponse(response, WRONG_TYPE_SIGNATURE);
        } else if (exception.equals(
            WRONG_TYPE_TOKEN.getMessage())) { // "지원되지 않는 형식이나 구성의 JWT 토큰입니다." // "유효하지 않은 구성의 JWT 토큰입니다.
            log.info("JwtAuthenticationEntryPoint : " + WRONG_TYPE_TOKEN);
            setResponse(response, WRONG_TYPE_SIGNATURE);
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
