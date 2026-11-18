package com.yunho.shop.entity.product;

import com.yunho.shop.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    /** Qdrant point id와 같은 값 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String name;
    private String description;
    /** 원 단위 판매가 */
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status")
    private SaleStatus saleStatus;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductOption> options = new ArrayList<>();

    @Builder
    public Product(Category category, Brand brand, String name, String description, int price) {
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.price = price;
        this.saleStatus = SaleStatus.ON_SALE;
    }

    // ---- 연관관계 편의 메서드 ----
    public void addImage(ProductImage image) {
        images.add(image);
        image.assignProduct(this);
    }

    public void addOption(ProductOption option) {
        options.add(option);
        option.assignProduct(this);
    }

    public Optional<ProductImage> getMainImage() {
        return images.stream().filter(ProductImage::isMain).findFirst();
    }

    // ---- 비즈니스 메서드 ----
    public void update(Category category, Brand brand, String name, String description, int price) {
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void changeSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    public void delete() {
        this.saleStatus = SaleStatus.HIDDEN;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
