package com.zerobase.wishmarket.domain.user.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// @Target : 어노테이션 생성 위치 지정 - 메소드의 파라미터로 선언된 객체에서만 사용 가능
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
// 어노테이션 클래스 지정 -> @LoginUserInfo 생성
public @interface LoginUserInfo {
}
