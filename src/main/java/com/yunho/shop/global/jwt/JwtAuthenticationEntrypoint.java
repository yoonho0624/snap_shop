package com.yunho.shop.global.jwt;

import com.yunho.shop.global.exception.ErrorCode;
import com.yunho.shop.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 로그인이 필요한 주소에 인증 없이 접근했을 때 401 응답을 만든다.
 * JwtAuthenticationFilter가 남긴 메모를 보고 실패 이유를 구분한다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntrypoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 필터가 남긴 메모가 있으면 그 이유(만료/위조), 없으면 토큰을 안 보낸 것
        ErrorCode code = request.getAttribute(JwtAuthenticationFilter.EXCEPTION_ATTRIBUTE) instanceof ErrorCode jwtError ? jwtError : ErrorCode.UNAUTHORIZED;

        response.setStatus(code.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(code));
    }
}
