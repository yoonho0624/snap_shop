package com.yunho.shop.global.file;

/** 저장된 이미지의 접근 주소와 SHA-256 해시 */
public record StoredImage(String url, String hash) {
}
