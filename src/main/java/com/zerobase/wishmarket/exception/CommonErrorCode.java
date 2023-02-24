package com.zerobase.wishmarket.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    REDIS_PUT_EMPTY_KEY(HttpStatus.BAD_REQUEST, "Empty Key를 입력하였습니다."),
    REDIS_PUT_FAIL(HttpStatus.BAD_REQUEST, "잘못된 Key를 입력하였습니다."),
    DUPLICATION_KEY(HttpStatus.BAD_REQUEST, "중복된 Key입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "업로드한 파일이 없습니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일 업로드 용량을 초과하였습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    CREDENTIALS_DO_NOT_EXIST(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다."),
    WRONG_TYPE_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 구성의 JWT 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다. 재 로그인 해주세요~"),
    NOT_EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Access Token이 아닙니다."),
    NOT_VERIFICATION_AUTH_CODE(HttpStatus.UNAUTHORIZED, "비정상적인 접근!! 인증 코드 검증이 완료되지 않았습니다."),
    NOT_VERIFICATION_LOGOUT(HttpStatus.UNAUTHORIZED, "로그아웃 된 Token으로 접근하셨습니다. 재로그인 부탁드립니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    EXPIRED_KEY(HttpStatus.NOT_FOUND, "해당 Key는 이미 만료되었습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    /* 500 Internal Server Error : 서버가 처리 방법을 모르는 상황이 발생. 서버는 아직 처리 방법을 알 수 없음.*/
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 전송에 실패하였습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패하였습니다.");
    private final HttpStatus errorCode;
    private final String message;
}
