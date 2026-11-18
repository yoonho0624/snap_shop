package com.yunho.shop.entity.common;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** member_address, orders가 함께 쓰는 배송 정보 (값 타입) */
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {
    private String recipient;
    private String phone;
    private String zipcode;
    private String address1;
    private String address2;

    @Builder
    public DeliveryAddress(String recipient, String phone, String zipcode, String address1, String address2) {
        this.recipient = recipient;
        this.phone = phone;
        this.zipcode = zipcode;
        this.address1 = address1;
        this.address2 = address2;
    }
}
