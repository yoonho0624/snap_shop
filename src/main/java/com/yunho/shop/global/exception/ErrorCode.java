package com.yunho.shop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 파일
    IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "이미지를 1장 이상 올려 주세요."),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST, "이미지 파일만 올릴 수 있습니다."),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 너무 큽니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),

    // 상품
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "브랜드를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    DUPLICATE_OPTION(HttpStatus.BAD_REQUEST, "같은 색상·사이즈 옵션이 중복되었습니다."),

    // 회원
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

    // 인증
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다. 다시 로그인해 주세요."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 장바구니
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션을 찾을 수 없습니다."),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "판매 중인 상품이 아닙니다."),
    CART_QUANTITY_EXCEEDED(HttpStatus.BAD_REQUEST, "한 상품은 최대 99개까지 담을 수 있습니다."),

    // 주문
    ORDER_ITEM_EMPTY(HttpStatus.BAD_REQUEST, "주문할 상품을 선택해 주세요."),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.CONFLICT, "배송이 시작된 주문은 최소할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
