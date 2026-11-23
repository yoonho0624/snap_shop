package com.yunho.shop.service.member;

import com.yunho.shop.dto.member.MemberResponse;
import com.yunho.shop.entity.member.Member;
import com.yunho.shop.global.exception.BusinessException;
import com.yunho.shop.global.exception.ErrorCode;
import com.yunho.shop.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    // 내 정보 조회 : 토큰에서 꺼낸 회원 ID로 조회
    public MemberResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .filter(m -> !m.isWithdrawn())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }
}
