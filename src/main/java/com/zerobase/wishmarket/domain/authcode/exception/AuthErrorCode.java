package com.zerobase.wishmarket.domain.authcode.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */

    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "잘못된 인증코드를 입력하였습니다."),
    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    /* 500 Internal Server Error : 서버가 처리 방법을 모르는 상황이 발생. 서버는 아직 처리 방법을 알 수 없음.*/

    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다."),
    CANNOT_FIND_MAIL_TEMPLATE(HttpStatus.INTERNAL_SERVER_ERROR, "Mail 템플릿을 찾을 수 없습니다.")

    ;

    private final HttpStatus errorCode;
    private final String message;
}
