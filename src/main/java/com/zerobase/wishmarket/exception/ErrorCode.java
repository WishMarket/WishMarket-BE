package com.zerobase.wishmarket.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getErrorCode();
    
    String getMessage();

}
