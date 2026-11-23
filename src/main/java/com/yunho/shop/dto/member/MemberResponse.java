package com.yunho.shop.dto.member;

import com.yunho.shop.entity.member.Member;
import com.yunho.shop.entity.member.Role;

import java.time.LocalDateTime;

// 회원 정보 응답(회원가입 결과, 내 정보 조회)
public record MemberResponse(
        Long memberId,
        String email,
        String name,
        String phone,
        Role role,
        LocalDateTime createdAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getRole(),
                member.getCreatedAt()
        );
    }
}
