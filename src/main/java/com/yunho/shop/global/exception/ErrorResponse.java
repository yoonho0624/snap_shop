package com.yunho.shop.global.exception;

/** 모든 에러 응답 형식: { "code": "PRODUCT_NOT_FOUND", "message": "상품을 찾을 수 없습니다." } */
public record ErrorResponse(String code, String message) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.name(), message);
    }
}
