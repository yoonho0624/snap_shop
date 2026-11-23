package com.yunho.shop.global.exception;

import lombok.Getter;

/** 서비스에서 throw new BusinessException(ErrorCode.XXX)로 던지면 GlobalExceptionHandler가 응답으로 바꿈 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
