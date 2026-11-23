package com.yunho.shop.dto.product;

import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductImage;
import com.yunho.shop.entity.product.SaleStatus;

/** 상품 목록의 카드 하나 */
public record ProductSummaryResponse(
        Long productId,
        String name,
        int price,
        String categoryName,
        String brandName,
        String mainImageUrl,
        SaleStatus saleStatus
) {

    public static ProductSummaryResponse from(Product product) {
        return new ProductSummaryResponse(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getCategory().getName(),
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getMainImage().map(ProductImage::getImageUrl).orElse(null),
                product.getSaleStatus()
        );
    }
}
