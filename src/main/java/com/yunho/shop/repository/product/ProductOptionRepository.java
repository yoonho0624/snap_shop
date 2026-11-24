package com.yunho.shop.repository.product;

import com.yunho.shop.entity.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    // 옵션 + 상품을 한 번에(판매 상태 확인, 응답 만들기용)
    @Query("select po from ProductOption po join fetch po.product where po.productOptionId = :productOptionId")
    Optional<ProductOption> findWithProductByProductOptionId(@Param("productOptionId") Long productOptionId);
}
