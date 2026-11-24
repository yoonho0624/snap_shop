package com.yunho.shop.repository.product;

import com.yunho.shop.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 목록 조회: 삭제·숨김 상품 제외, categoryId가 null이면 전체.
     * category, brand는 @EntityGraph로 한 번에 가져와서 N+1을 막는다.
     * images(대표 이미지)는 컬렉션이라 페이징과 fetch join을 같이 쓸 수 없어서
     * default_batch_fetch_size 설정으로 IN 쿼리 한 번에 가져온다.
     */
    @EntityGraph(attributePaths = {"category", "brand"})
    @Query(value = """
            select p from Product p
            where p.deletedAt is null
              and p.saleStatus <> com.yunho.shop.entity.product.SaleStatus.HIDDEN
              and (:categoryId is null or p.category.categoryId = :categoryId)
            """,
            countQuery = """
            select count(p) from Product p
            where p.deletedAt is null
              and p.saleStatus <> com.yunho.shop.entity.product.SaleStatus.HIDDEN
              and (:categoryId is null or p.category.categoryId = :categoryId)
            """)
    Page<Product> findDisplayProducts(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * 상세 조회: category, brand, images를 fetch join으로 한 번에.
     * options까지 같이 fetch join하면 List 두 개를 동시에 가져오게 돼서
     * MultipleBagFetchException이 나므로, options는 batch fetch로 따로 가져온다.
     */
    @Query("""
            select p from Product p
            join fetch p.category
            left join fetch p.brand
            left join fetch p.images
            where p.productId = :productId
              and p.deletedAt is null
            """)
    Optional<Product> findDetailByProductId(@Param("productId") Long productId);
}
