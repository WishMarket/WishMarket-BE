package com.zerobase.wishmarket.domain.funding.exception;

import com.zerobase.wishmarket.exception.GlobalException;
import lombok.Getter;

@Getter
public class FundingException extends GlobalException {


    private final FundingErrorCode fundingErrorCode;

    public FundingException(FundingErrorCode errorCode) {
        super(errorCode);
        this.fundingErrorCode = errorCode;
    }


}
