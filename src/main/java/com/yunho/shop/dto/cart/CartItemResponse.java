package com.yunho.shop.dto.cart;

import com.yunho.shop.entity.order.CartItem;
import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductImage;
import com.yunho.shop.entity.product.ProductOption;
import com.yunho.shop.entity.product.SaleStatus;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        Long productOptionId,
        String productName,
        String optionName,
        String imageUrl,
        int unitPrice,
        int quantity,
        int totalPrice,
        int stock,
        boolean orderable
) {
    public static CartItemResponse from(CartItem cartItem) {
        ProductOption option = cartItem.getProductOption();
        Product product = option.getProduct();

        int unitPrice = option.getUnitPrice();
        int quantity = cartItem.getQuantity();

        return new CartItemResponse(
                cartItem.getCartItemId(),
                product.getProductId(),
                option.getProductOptionId(),
                product.getName(),
                option.getOptionName(),
                product.getMainImage().map(ProductImage::getImageUrl).orElse(null),
                unitPrice,
                quantity,
                unitPrice * quantity,
                option.getStock(),
                !product.isDeleted() && product.getSaleStatus() == SaleStatus.ON_SALE && option.getStock() >= quantity
        );
    }
}
