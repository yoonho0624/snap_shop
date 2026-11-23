package com.yunho.shop.service.member;

import com.yunho.shop.dto.member.LoginRequest;
import com.yunho.shop.dto.member.MemberResponse;
import com.yunho.shop.dto.member.SignupRequest;
import com.yunho.shop.dto.member.TokenResponse;
import com.yunho.shop.entity.member.Member;
import com.yunho.shop.entity.member.Role;
import com.yunho.shop.global.exception.BusinessException;
import com.yunho.shop.global.exception.ErrorCode;
import com.yunho.shop.global.jwt.JwtProvider;
import com.yunho.shop.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입 : 이메일 중복 학인 -> 비밀번호 암호화 -> 저장
    @Transactional
    public MemberResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());

        // 1차 확인 : 대부분의 중복은 여기서 걸러짐
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .phone(request.phone())
                .role(Role.USER)
                .build();

        try {
            return MemberResponse.from(memberRepository.save(member));
        } catch (DataIntegrityViolationException e) {
            // 2차 방어 : 동시에 같은 이메일로 가입해서 DB UNIQUE 제약에 걸린 경우
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, e);
        }
    }

    // 로그인 : 회원 조회 -> 탈퇴 여부 -> 비밀번호 비교 -> 토큰 발급
    public TokenResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

        // 탈퇴한 회원, 비밀번호 틀림 모두 같은 에러 -> 어떤 이메일이 가입돼 있는지 숨김
        if (member.isWithdrawn()
            || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        String accessToken = jwtProvider.createAccessToken(member.getMemberId(), member.getRole());
        return TokenResponse.of(accessToken, jwtProvider.getAccessTokenExpirationMs());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
