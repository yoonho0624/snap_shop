package com.yunho.shop.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 30, message = "이메일은 30자 이하로 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해 주세요."
        )
        String password,

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
        String name,

        // 선택 사항
        @Pattern(
                regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "전화번호는 010-1234-5678 형식으로 입력해 주세요."
        )
        String phone
        ) {
    @Override
    public String toString() {
        return "SignupRequest[email=" + email + ", password=****, name=" + name + ", phone=" + phone + "]";
    }
}
