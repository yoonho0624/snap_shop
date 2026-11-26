package com.yunho.shop.repository.order;

import com.yunho.shop.entity.order.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 내 주문 목록. 주문 상품은 fetch join 하지 않고 batch fetch로 가져온다
    Page<Order> findByMember_memberId(Long memberId, Pageable pageable);

    // 주문 상세 : 주문 상품까지 한 번에
    @Query("""
        select o from Order o
        join fetch o.orderItems
        where o.orderNo = :orderNo and o.member.memberId = :memberId
        """)
    Optional<Order> findWithItemsByOrderNoAndMember_memberId(@Param("orderNo") String orderNo,
                                                             @Param("memberId") Long memberId);

    // 상태 변경용(취소, 결제 승인) : 주문 행에 쓰기 락을 건다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.orderNo = :orderNo and o.member.memberId = :memberId")
    Optional<Order> findForUpdateByOrderNoAndMember_memberId(@Param("orderNo") String orderNo,
                                                             @Param("memberId") Long memberId);
}
