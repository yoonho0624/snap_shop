package com.yunho.shop.dto.cart;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        int orderableTotalPrice
) {
    public static CartResponse from(List<CartItemResponse> items) {
        int total = items.stream()
                .filter(CartItemResponse::orderable)
                .mapToInt(CartItemResponse::totalPrice)
                .sum();
        return new CartResponse(items, total);
    }
}
