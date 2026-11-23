package com.yunho.shop.global.config;

import com.yunho.shop.global.jwt.JwtAccessDeniedHandler;
import com.yunho.shop.global.jwt.JwtAuthenticationEntrypoint;
import com.yunho.shop.global.jwt.JwtAuthenticationFilter;
import com.yunho.shop.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntrypoint authenticationEntrypoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. JWT 방식에서 필요 없는 기본 기능 끄기
                .csrf(AbstractHttpConfigurer::disable) // csrf : 다른 사이트에서 몰래 요청 보내는 공격 방어
                .formLogin(AbstractHttpConfigurer::disable) // formLogin : /login 기본 로그인 화면 제공
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic : 요청마다 아이디, 비밀번호를 Header에 담아 보내는 방식
                .logout(AbstractHttpConfigurer::disable) // logout : /logout 기본 로그아웃

                // 2. 세션을 만들지 않음(로그인 상태는 토큰으로만 판단)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 주소별 접근 규칙(위에서부터 순서대로 검사)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // 4. 401, 403 응답을 우리 형식으로
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntrypoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 5. JWT 필터를 Security 필터 체인에 끼워 넣기
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 비밀번호 암호화(회원가입 시 암호화, 로그인 시 비교)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
