package com.yunho.shop.repository.product;

import com.yunho.shop.entity.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    // 옵션 + 상품을 한 번에(판매 상태 확인, 응답 만들기용)
    @Query("select po from ProductOption po join fetch po.product where po.productOptionId = :productOptionId")
    Optional<ProductOption> findWithProductByProductOptionId(@Param("productOptionId") Long productOptionId);

    // 재고 차감. 재고가 충분할 때만 빼고, 바뀐 행 수를 돌려준다.
    @Modifying(flushAutomatically = true)
    @Query("""
            update ProductOption po
            set po.stock = po.stock - :quantity,
                po.version = po.version + 1
            where po.productOptionId = :productOptionId
                and :quantity > 0
                and po.stock >= :quantity
    """)
    int decreaseStock(@Param("productOptionId") Long productOptionId, @Param("quantity") int quantity);

    // 재고 복구(주문 취소, 결제 만료).
    @Modifying(flushAutomatically = true)
    @Query("""
            update ProductOption po
            set po.stock = po.stock + :quantity,
                po.version = po.version + 1
            where po.productOptionId = :productOptionId
                and :quantity > 0       
           """)
    int increaseStock(@Param("productOptionId") Long productOptionId, @Param("quantity") int quantity);
}
