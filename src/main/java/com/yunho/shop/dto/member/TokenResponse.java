package com.yunho.shop.dto.member;

public record TokenResponse(String tokenType, String accessToken, long expiresIn) {
    private static final String BEARER = "Bearer";

    public static TokenResponse of(String accessToken, Long expirationMs) {
        return new TokenResponse(BEARER, accessToken, expirationMs / 1000);
    }
}
