package com.yunho.shop.controller.product;

import com.yunho.shop.dto.product.ProductCreateRequest;
import com.yunho.shop.dto.product.ProductCreateResponse;
import com.yunho.shop.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

/** 관리자 전용. 2단계에서 Spring Security로 ADMIN만 접근하게 막을 예정 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductCreateResponse> createProduct(
            @Valid @RequestPart("data") ProductCreateRequest request,
            @RequestPart("images") List<MultipartFile> images
    ) {
        Long productId = productService.createProduct(request, images);
        return ResponseEntity
                .created(URI.create("/api/products/" + productId))
                .body(new ProductCreateResponse(productId));
    }
}
