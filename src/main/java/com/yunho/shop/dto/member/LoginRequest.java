package com.yunho.shop.dto.member;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
    /** 로그에 비밀번호가 찍히지 않게 가림 */
    @Override
    public String toString() {
        return "LoginRequest[email=" + email + ", password=****]";
    }
}
