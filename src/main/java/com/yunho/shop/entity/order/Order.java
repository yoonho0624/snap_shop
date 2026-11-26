package com.yunho.shop.entity.order;

import com.yunho.shop.entity.common.DeliveryAddress;
import com.yunho.shop.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** order는 MySQL 예약어라서 테이블명은 orders */
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    /** 외부 노출용 주문번호 */
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "total_price")
    private int totalPrice;

    /** 배송 정보 스냅샷 */
    @Embedded
    private DeliveryAddress deliveryAddress;

    @CreatedDate
    @Column(name = "ordered_at", updatable = false)
    private LocalDateTime orderedAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderItemId ASC")
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(String orderNo, Member member, DeliveryAddress deliveryAddress) {
        this.orderNo = orderNo;
        this.member = member;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.PENDING;
        this.totalPrice = 0;
    }

    // ---- 연관관계 편의 메서드 ----
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);
        this.totalPrice += orderItem.getTotalPrice();
    }

    // ---- 상태 변경 ----
    public void pay() {
        changeStatus(OrderStatus.PENDING, OrderStatus.PAID);
    }

    public void ship() {
        changeStatus(OrderStatus.PAID, OrderStatus.SHIPPING);
    }

    public void deliver() {
        changeStatus(OrderStatus.SHIPPING, OrderStatus.DELIVERED);
    }

    // PENDING, PAID일 때만 취소할 수 있다
    public boolean isCancelable() {
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }

    public boolean isCanceled() {
        return status == OrderStatus.CANCELED;
    }

    public void cancel() {
        if (!isCancelable()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다. (현재 상태: " + status + ")");
        }
        this.status = OrderStatus.CANCELED;
    }

    private void changeStatus(OrderStatus expected, OrderStatus next) {
        if (status != expected) {
            throw new IllegalStateException(status + " 상태에서는 " + next + "(으)로 바꿀 수 없습니다.");
        }
        this.status = next;
    }
}
