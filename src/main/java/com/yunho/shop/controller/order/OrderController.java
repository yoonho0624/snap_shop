package com.yunho.shop.controller.order;

import com.yunho.shop.dto.common.PageResponse;
import com.yunho.shop.dto.order.OrderCreateRequest;
import com.yunho.shop.dto.order.OrderResponse;
import com.yunho.shop.dto.order.OrderSummaryResponse;
import com.yunho.shop.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 로그인한 회원의 주문
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private static final int MAX_PAGE_SIZE = 50;
    private final OrderService orderService;

    // 장바구니에서 선택한 상품 주문
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@AuthenticationPrincipal Long memberId,
                                @Valid @RequestBody OrderCreateRequest request) {
        return orderService.createFromCart(memberId, request);
    }

    // 내 주문 목록
    @GetMapping
    public PageResponse<OrderSummaryResponse> getMyOrders(@AuthenticationPrincipal long memberId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "orderId")
        );
        return PageResponse.from(orderService.getMyOrders(memberId, pageable));
    }

    // 내 주문 상세
    @GetMapping("/{orderNo}")
    public OrderResponse getMyOrder(@AuthenticationPrincipal Long memberId,
                                    @PathVariable String orderNo) {
        return orderService.getMyOrder(memberId, orderNo);
    }

    // 주문 취소
    @PostMapping("/{orderNo}/cancel")
    public OrderResponse cancel(@AuthenticationPrincipal Long memberId,
                                @PathVariable String orderNo) {
        return orderService.cancel(memberId, orderNo);
    }
}
