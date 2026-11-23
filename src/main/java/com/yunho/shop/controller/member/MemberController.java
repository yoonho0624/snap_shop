package com.yunho.shop.controller.member;

import com.yunho.shop.dto.member.MemberResponse;
import com.yunho.shop.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 로그인한 회원만 쓰는 API(SecurityConfig의 anyRequest().authenticated()에 해당)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    // 내 정보 조회
    @GetMapping("/me")
    public MemberResponse getMyInfo(@AuthenticationPrincipal Long memberId) {
        return memberService.getMyInfo(memberId);
    }
}
