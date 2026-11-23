package com.yunho.shop.global.jwt;

import com.yunho.shop.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 요청마다 Authorization 헤더의 JWT를 검사해서,
 * 통과하면 SecurityContext에 "로그인한 회원"으로 등록한다.
 *
 * @Component를 붙이지 않는다. SecurityConfig에서 직접 생성해서 Security 필터 체인에만 넣는다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰 검사 실패 이유를 EntryPoint(8번)에 넘길 때 쓰는 이름
    public static final String EXCEPTION_ATTRIBUTE = "jwtException";

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            try {
                Claims claims = jwtProvider.parseClaims(token);
                setAuthentication(claims);
            } catch (ExpiredJwtException e) {
                // 자식 예외(만료)를 먼저 잡아야 함
                request.setAttribute(EXCEPTION_ATTRIBUTE, ErrorCode.EXPIRED_TOKEN);
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(EXCEPTION_ATTRIBUTE, ErrorCode.INVALID_TOKEN);
            }
        }

        // 토큰이 없거나 틀려도 여기서 막지 않고 다음으로 넘김.
        // 로그인이 필요한 주소인지는 SecurityConfig 규칙이 판단한다.
        filterChain.doFilter(request, response);
    }

    // "Authorization : Bearer xxx.yyy.zzz"에서 토큰 부분만 꺼냄
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void setAuthentication(Claims claims) {
        Long memberId = Long.valueOf(claims.getSubject());
        String role = claims.get(JwtProvider.ROLE_CLAIM, String.class);
        if (role == null) {
            throw new IllegalArgumentException("role claim이 없습니다.");
        }

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                memberId, // principal : 누구인지
                null, // credentials : 비밀번호(이미 검증됐으니 필요 없음)
                List.of(new SimpleGrantedAuthority("ROLE_" + role)) // authorities : 권한
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
