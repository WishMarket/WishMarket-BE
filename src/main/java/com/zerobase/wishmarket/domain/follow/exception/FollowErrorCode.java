package com.zerobase.wishmarket.domain.follow.exception;

import com.zerobase.wishmarket.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorCode implements ErrorCode {
    ALREADY_FOLLOWING_USER(HttpStatus.BAD_REQUEST, "이미 팔로우 중인 회원입니다."),
    CANNOT_FOLLOW_YOURSELF(HttpStatus.BAD_REQUEST, "본인은 팔로우할 수 없습니다."),
    CANNOT_UNFOLLOW_YOURSELF(HttpStatus.BAD_REQUEST, "본인은 언팔로우할 수 없습니다.")


    ;


    private final HttpStatus errorCode;
    private final String message;
}
