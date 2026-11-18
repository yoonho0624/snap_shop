package com.yunho.shop.entity.member;

import com.yunho.shop.entity.common.BaseTimeEntity;
import com.yunho.shop.entity.common.DeliveryAddress;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddress extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberAddressId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Builder
    public MemberAddress(Member member, DeliveryAddress deliveryAddress, boolean isDefault) {
        this.member = member;
        this.deliveryAddress = deliveryAddress;
        this.isDefault = isDefault;
    }

    public void changeAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void markDefault() {
        this.isDefault = true;
    }

    public void unmarkDefault() {
        this.isDefault = false;
    }
}
