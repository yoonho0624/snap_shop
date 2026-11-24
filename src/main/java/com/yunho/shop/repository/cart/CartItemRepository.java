package com.yunho.shop.repository.cart;

import com.yunho.shop.entity.order.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 담기 : 같은 옵션이 이미 있는지 확인
    Optional<CartItem> findByMember_MemberIdAndProductOption_ProductOptionId(Long memberId, Long productOptionId);

    // 수량 변경, 삭제 : 내 장바구니 상품인지 함께 확인
    Optional<CartItem> findByCartItemIdAndMember_MemberId(Long cartItemId, Long memberId);

    // 장바구니 목록 : 옵션과 상품까지 한 번에
    @Query("""
            select ci from CartItem ci
            join fetch ci.productOption po
            join fetch po.product p
            where ci.member.memberId = :memberId
            order by ci.cartItemId desc
    """)
    List<CartItem> findAllWithProductByMember_MemberId(@Param("memberId") Long memberId);

    // 선택 주문 : 고른 장바구니 상품만, 내 것만
    @Query("""
        select ci from CartItem ci
        join fetch ci.productOption po
        join fetch po.product p
        where ci.cartItemId in :ids and ci.member.memberId = :memberId
        """)
    List<CartItem> findAllWithProductByCartItemIdInAndMember_MemberId(@Param("ids") Collection<Long> ids,
                                                                      @Param("memberId") Long memberId);

    // 주문이 끝난 상품을 장바구니에서 한 번에 지우기
    @Modifying
    @Query("delete from CartItem ci where ci.cartItemId in :ids and ci.member.memberId = :memberId")
    int deleteAllByCartItemIdAndMember_MemberId(@Param("ids") Collection<Long> ids,
                                                @Param("memberId") Long memberId);
}
