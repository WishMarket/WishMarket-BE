package com.zerobase.wishmarket.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    /* 400 BAD_REQUEST : 잘못된 요청 */
    REDIS_PUT_EMPTY_KEY(HttpStatus.BAD_REQUEST, "Empty Key를 입력하였습니다."),
    REDIS_PUT_FAIL(HttpStatus.BAD_REQUEST, "잘못된 Key를 입력하였습니다."),
    DUPLICATION_KEY(HttpStatus.BAD_REQUEST, "중복된 Key입니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    EXPIRED_KEY(HttpStatus.NOT_FOUND, "해당 Key는 이미 만료되었습니다.")

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    /* 500 Internal Server Error : 서버가 처리 방법을 모르는 상황이 발생. 서버는 아직 처리 방법을 알 수 없음.*/

    ;

    private final HttpStatus errorCode;
    private final String message;
}