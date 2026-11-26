package com.yunho.shop.dto.order;

import com.yunho.shop.entity.order.OrderItem;
import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductImage;

public record OrderItemResponse(
        Long productId,
        String productName,
        String optionName,
        int unitPrice,
        int quantity,
        int totalPrice,
        String imageUrl
) {
    public static OrderItemResponse from(OrderItem item) {
        Product product = item.getProductOption().getProduct();
        return new OrderItemResponse(
                product.getProductId(),
                item.getProductName(),
                item.getOptionName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getTotalPrice(),
                product.getMainImage().map(ProductImage::getImageUrl).orElse(null)
        );
    }
}
