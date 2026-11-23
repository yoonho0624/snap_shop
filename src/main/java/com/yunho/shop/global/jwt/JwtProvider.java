package com.yunho.shop.global.jwt;

import com.yunho.shop.entity.member.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT를 만들고 검사한다.
 * Spring Security는 모르는 순수한 토큰 도구. Security와 연결하는 일은 JwtAuthenticationFilter가 한다.
 */
@Component
public class JwtProvider {
    public static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey;

    @Getter
    private final long accessTokenExpirationMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs
    ) {
        // Base64 문자열 -> 바이트 -> HMAC-SHA키. 32바이트 미만이면 여기서 WeakKeyException이 나서 앱이 안뜸
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    // 로그인 성공 시 Access Token 발급
    public String createAccessToken(Long memberId, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 토큰의 주인 : 회원 ID
                .claim(ROLE_CLAIM, role.name()) // 권한 : "USER" 또는 "ADMIN"
                .issuedAt(now) // 발급 시각
                .expiration(expiration) //만료 시각
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    /**
     * 서명과 만료 시간을 검사하고, 통과하면 내용(Claims)을 돌려준다.
     *
     * @throws io.jsonwebtoken.ExpiredJwtException 만료된 토큰
     * @throws io.jsonwebtoken.JwtException        서명이 틀리거나 형식이 깨진 토큰
     * @throws IllegalArgumentException            null이거나 빈 문자열
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 이 키로 서명을 검사
                .build()
                .parseSignedClaims(token) // 검사 실행
                .getPayload(); // 통과하면 내용(Claims) 꺼내기
    }
}
