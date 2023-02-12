package com.zerobase.wishmarket.domain.user.config;

import com.zerobase.wishmarket.domain.user.model.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    // supportsParameter() : 컨트롤러 메소드의 특정 파라미터 지원 여부 판단
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUserInfo.class) != null;
        boolean isUserClass = OAuthUserInfo.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }


    @Override
    // resolveArgument() : 세션에서 객체를 가져와서 파라미터에 전달할 객체 생성
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }
}
