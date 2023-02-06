package com.zerobase.wishmarket.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        GlobalException.class
    })
    public ResponseEntity<ExceptionResponse> globalRequestException(final GlobalException e) {
        log.warn("api Exception : {}", e.getErrorCode());
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(e.getMessage(), e.getErrorCode()));
    }

    @Getter
    @AllArgsConstructor
    public static class ExceptionResponse {

        private String message;
        private GlobalErrorCode errorCode;
    }


}