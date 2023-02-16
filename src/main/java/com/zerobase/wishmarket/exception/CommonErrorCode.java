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
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED,  "인증 정보가 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    EXPIRED_KEY(HttpStatus.NOT_FOUND, "해당 Key는 이미 만료되었습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    /* 500 Internal Server Error : 서버가 처리 방법을 모르는 상황이 발생. 서버는 아직 처리 방법을 알 수 없음.*/
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 전송에 실패하였습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패하였습니다.")
    ;
    private final HttpStatus errorCode;
    private final String message;
}
