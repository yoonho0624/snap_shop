package com.yunho.shop.dto.order;

import com.yunho.shop.entity.common.DeliveryAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DeliveryAddressRequest(
    @Size(message = "받는 분 이름은 50자 이하여야 합니다.", max = 50)
    @NotBlank(message = "받는 분 이름을 입력해 주세요.")
    String recipient,

    @Pattern(message = "휴대폰 번호 형식이 올바르지 않습니다.", regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$")
    @NotBlank(message = "연락처를 입력해 주세요.")
    String phone,

    @Pattern(message = "우편번호는 숫자 5자리여야 합니다.", regexp = "^\\d{5}$")
    @NotBlank(message = "우편번호를 입력해 주세요")
    String zipcode,

    @Size(message = "주소는 200자 이하여야 합니다.", max = 200)
    @NotBlank(message = "주소를 입력해 주세요.")
    String address1,

    @Size(message = "상세 주소는 200자 이하여야 합니다.")
    String address2
) {
    public DeliveryAddress toEntity() {
        return DeliveryAddress.builder()
                .recipient(recipient)
                .phone(phone)
                .zipcode(zipcode)
                .address1(address1)
                .address2(address2)
                .build();
    }
}