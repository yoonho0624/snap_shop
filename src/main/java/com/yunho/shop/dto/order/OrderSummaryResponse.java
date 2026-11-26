package com.yunho.shop.dto.order;

import com.yunho.shop.entity.order.Order;
import com.yunho.shop.entity.order.OrderItem;
import com.yunho.shop.entity.order.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
        String orderNo,
        OrderStatus status,
        int totalPrice,
        LocalDateTime orderedAt,
        String title,
        int itemCount,
        String imageUrl
) {
    public static OrderSummaryResponse from(Order order) {
        OrderItem first = order.getOrderItems().getFirst();
        int count = order.getOrderItems().size();

        String title = (count == 1)
                ? first.getProductName()
                : first.getProductName() + " 외 " + (count -1) + "건";

        return new OrderSummaryResponse(
                order.getOrderNo(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderedAt(),
                title,
                count,
                OrderItemResponse.from(first).imageUrl()
        );
    }
}
