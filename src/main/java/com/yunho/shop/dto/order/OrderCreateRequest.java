package com.yunho.shop.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(
        @NotEmpty(message = "주문할 상품을 선택해 주세요.")
        @Size(max = 50, message = "한 번에 50개까지 주문할 수 있습니다.")
        List<@NotNull Long> cartItemIds,

        @NotNull(message = "배송지를 입력해 주세요.")
        @Valid
        DeliveryAddressRequest deliveryAddress
) {
}
