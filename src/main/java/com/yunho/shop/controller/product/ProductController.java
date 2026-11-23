package com.yunho.shop.controller.product;

import com.yunho.shop.dto.common.PageResponse;
import com.yunho.shop.dto.product.ProductDetailResponse;
import com.yunho.shop.dto.product.ProductSummaryResponse;
import com.yunho.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /** 예: GET /api/products?categoryId=2&page=0&size=20&sort=price,asc */
        @GetMapping
        public PageResponse<ProductSummaryResponse> getProducts(
                @RequestParam(required = false) Long categoryId,
                @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
            return productService.getProducts(categoryId, pageable);
        }

        @GetMapping("/{productId}")
        public ProductDetailResponse getProduct(@PathVariable Long productId) {
            return productService.getProduct(productId);
    }
}
