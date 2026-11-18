package com.yunho.shop.entity.order;

import com.yunho.shop.entity.product.ProductOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    /** 주문 당시 상품명 */
    private String productName;
    /** 주문 당시 옵션 (예: 블랙 / M) */
    private String optionName;
    /** 주문 당시 단가(추가금 포함) */
    private int unitPrice;
    private int quantity;

    private OrderItem(ProductOption productOption, String productName, String optionName,
                      int unitPrice, int quantity) {
        this.productOption = productOption;
        this.productName = productName;
        this.optionName = optionName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** 주문 시점의 상품명·옵션명·가격을 스냅샷으로 저장한다 (재고 차감은 별도 조건부 UPDATE) */
    public static OrderItem create(ProductOption option, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        return new OrderItem(
                option,
                option.getProduct().getName(),
                option.getOptionName(),
                option.getUnitPrice(),
                quantity
        );
    }

    /** Order.addOrderItem()에서만 호출 */
    void assignOrder(Order order) {
        this.order = order;
    }

    public int getTotalPrice() {
        return unitPrice * quantity;
    }
}
