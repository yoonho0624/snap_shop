package com.yunho.shop.service.order;

import com.yunho.shop.dto.order.OrderCreateRequest;
import com.yunho.shop.dto.order.OrderResponse;
import com.yunho.shop.dto.order.OrderSummaryResponse;
import com.yunho.shop.entity.common.DeliveryAddress;
import com.yunho.shop.entity.order.CartItem;
import com.yunho.shop.entity.order.Order;
import com.yunho.shop.entity.order.OrderItem;
import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductOption;
import com.yunho.shop.entity.product.SaleStatus;
import com.yunho.shop.global.exception.BusinessException;
import com.yunho.shop.global.exception.ErrorCode;
import com.yunho.shop.repository.cart.CartItemRepository;
import com.yunho.shop.repository.member.MemberRepository;
import com.yunho.shop.repository.order.OrderRepository;
import com.yunho.shop.repository.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;
    private final OrderNoGenerator orderNoGenerator;

    // ==== 주문 생성 ====

    // 장바구니에서 선택한 상품 주문
    @Transactional
    public OrderResponse createFromCart(Long memberId, OrderCreateRequest request) {
        Set<Long> cartItemIds = new HashSet<>(request.cartItemIds());

        List<CartItem> cartItems = cartItemRepository.findAllWithProductByCartItemIdInAndMember_MemberId(cartItemIds, memberId);
        if (cartItems.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        List<OrderLine> lines = cartItems.stream()
                .map(ci -> new OrderLine(ci.getProductOption(), ci.getQuantity()))
                .toList();

        Order order = placeOrder(memberId, lines, request.deliveryAddress().toEntity());

        cartItemRepository.deleteAllByCartItemIdAndMember_MemberId(cartItemIds, memberId);

        return OrderResponse.from(order);
    }

    // ==== 조회 ====

    // 내 주문 목록
    public Page<OrderSummaryResponse> getMyOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMember_memberId(memberId, pageable)
                .map(OrderSummaryResponse::from);
    }

    // 내 주문 상세
    public OrderResponse getMyOrder(Long memberId, String orderNo) {
        Order order = orderRepository.findWithItemsByOrderNoAndMember_memberId(orderNo, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponse.from(order);
    }

    // ==== 취소 ====

    // 회원이 직접 취소
    @Transactional
    public OrderResponse cancel(Long memberId, String orderNo) {
        Order order = orderRepository.findForUpdateByOrderNoAndMember_memberId(orderNo, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        cancelAndRestoreStock(order);

        return OrderResponse.from(order);
    }

    // ==== 내부 메서드 ====

    // 장바구니 주문과 바로 구매가 함께 쓰는 로직
    private Order placeOrder(Long memberId, List<OrderLine> lines, DeliveryAddress address) {
        // 1. 판매 중인지 확인(락을 잡기 전에 싼 검사부터)
        for (OrderLine line : lines) {
            validateOnSale(line.option().getProduct());
        }

        // 2, 재고 차감 : 옵션 ID 순서로(데드락 방지)
        lines.stream()
                .sorted(Comparator.comparing(line -> line.option().getProductOptionId()))
                .forEach(line -> {
                    int updated = productOptionRepository.decreaseStock(line.option().getProductOptionId(), line.quantity());
                    if (updated == 0) {
                        throw new BusinessException(ErrorCode.OUT_OF_STOCK);
                    }
                });

        // 3. 주문 + 주문 상품 저장(이름, 가격 스냅샷)
        Order order = Order.builder()
                .orderNo(orderNoGenerator.generate())
                .member(memberRepository.findById(memberId).filter(m -> !m.isWithdrawn()).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND)))
                .deliveryAddress(address)
                .build();
        lines.forEach(line -> order.addOrderItem(OrderItem.create(line.option(), line.quantity())));

        return orderRepository.save(order);
    }

    // 취소 + 재고 복구. 주문 행에 락이 걸린 상태에서 호출해야 한다
    private void cancelAndRestoreStock(Order order) {
        if (order.isCanceled()) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELED);
        }
        if (!order.isCancelable()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        // TODO 결제 파트 : PAID 주문이면 토스 결제 취소(환불)를 먼저 해야 한다.
        order.cancel();

        order.getOrderItems().stream()
                .sorted(Comparator.comparing(item -> item.getProductOption().getProductOptionId()))
                .forEach(item -> {
                    int updated = productOptionRepository.increaseStock(
                            item.getProductOption().getProductOptionId(), item.getQuantity());
                    if (updated == 0) {
                        // 정상이라면 일어날 수 없음 : 주문한 옵션이 DB에서 사라짐
                        throw new IllegalStateException("재고 복구 실패 : product_option_id=" + item.getProductOption().getProductOptionId());
                    }
                });
    }

    private void validateOnSale(Product product) {
        if (product.isDeleted() || product.getSaleStatus() != SaleStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }
    }

    // 주문 한 줄 : 어떤 옵션을 몇 개
    private record OrderLine(ProductOption option, int quantity) { }
}
