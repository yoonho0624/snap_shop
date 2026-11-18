package com.yunho.shop.entity.member;

import com.yunho.shop.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;
    private String password;
    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MemberStatus status;

    private LocalDateTime deletedAt;

    @Builder
    public Member(String email, String password, String name, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = (role != null) ? role : Role.USER;
        this.status = MemberStatus.ACTIVE;
    }

    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    /** 탈퇴 (soft delete) */
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return status == MemberStatus.WITHDRAWN;
    }
}
