package com.yunho.shop.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "product_option",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_option", columnNames = {"product_id", "color", "size"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_option_id")
    private Long productOptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private String color;
    /** S, M, L, 270 등 */
    private String size;
    /** 옵션 추가금 */
    private int extraPrice;
    private int stock;

    /** 낙관적 락 */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Builder
    public ProductOption(String color, String size, int extraPrice, int stock) {
        this.color = color;
        this.size = size;
        this.extraPrice = extraPrice;
        this.stock = stock;
    }

    /** Product.addOption()에서만 호출 */
    void assignProduct(Product product) {
        this.product = product;
    }

    /** 예: "블랙 / M" (order_item.option_name 스냅샷용) */
    public String getOptionName() {
        return color + " / " + size;
    }

    /** 판매가 + 옵션 추가금 */
    public int getUnitPrice() {
        return product.getPrice() + extraPrice;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        if (stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. (남은 재고: " + stock + ")");
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        this.stock += quantity;
    }

    public void update(int extraPrice, int stock) {
        this.extraPrice = extraPrice;
        this.stock = stock;
    }
}
