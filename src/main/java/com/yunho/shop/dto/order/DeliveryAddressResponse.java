package com.yunho.shop.dto.order;

import com.yunho.shop.entity.common.DeliveryAddress;

public record DeliveryAddressResponse(
        String recipient,
        String phone,
        String zipcode,
        String address1,
        String address2
) {
    public static DeliveryAddressResponse from(DeliveryAddress address) {
        return new DeliveryAddressResponse(
                address.getRecipient(),
                address.getPhone(),
                address.getZipcode(),
                address.getAddress1(),
                address.getAddress2()
        );
    }
}
