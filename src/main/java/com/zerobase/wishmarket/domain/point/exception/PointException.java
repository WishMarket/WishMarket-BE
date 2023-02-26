package com.zerobase.wishmarket.domain.point.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class PointException extends GlobalException {

    private final PointErrorCode pointErrorCode;

    public PointException(PointErrorCode pointErrorCode) {
        super(pointErrorCode);
        this.pointErrorCode = pointErrorCode;
    }
}
