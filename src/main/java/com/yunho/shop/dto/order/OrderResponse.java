package com.yunho.shop.dto.order;

import com.yunho.shop.entity.order.Order;
import com.yunho.shop.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderNo,
        OrderStatus status,
        int totalPrice,
        LocalDateTime orderedAt,
        DeliveryAddressResponse deliveryAddress,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderNo(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderedAt(),
                DeliveryAddressResponse.from(order.getDeliveryAddress()),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
