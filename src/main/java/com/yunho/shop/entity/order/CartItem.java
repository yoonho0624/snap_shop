package com.yunho.shop.entity.order;

import com.yunho.shop.entity.common.BaseTimeEntity;
import com.yunho.shop.entity.member.Member;
import com.yunho.shop.entity.product.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_item", columnNames = {"member_id", "product_option_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    // EAGER : 주문을 조회하면 회원도 무조건 조회,
    // LAZY : 주문만 먼저 조회하고 회원은 실제로 쓰는 순간에 조회
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    private int quantity;

    @Builder
    public CartItem(Member member, ProductOption productOption, int quantity) {
        validateQuantity(quantity);
        this.member = member;
        this.productOption = productOption;
        this.quantity = quantity;
    }

    /** 같은 옵션을 또 담으면 수량을 더한다 */
    public void addQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity += quantity;
    }

    public void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }
}
