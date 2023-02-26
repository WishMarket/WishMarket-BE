package com.zerobase.wishmarket.domain.follow.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class FollowException extends GlobalException {
    private final FollowErrorCode followErrorCode;

    public FollowException(FollowErrorCode followErrorCode){
        super(followErrorCode);
        this.followErrorCode = followErrorCode;
    }
}
