package com.yunho.shop.repository.member;

import com.yunho.shop.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /** 로그인: 이메일로 회원 찾기 */
    Optional<Member> findByEmail(String email);

    /** 회원가입: 이메일 중복 확인 */
    boolean existsByEmail(String email);
}
