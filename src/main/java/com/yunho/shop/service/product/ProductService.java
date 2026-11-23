package com.yunho.shop.service.product;

import com.yunho.shop.dto.common.PageResponse;
import com.yunho.shop.dto.product.ProductCreateRequest;
import com.yunho.shop.dto.product.ProductCreateRequest.OptionRequest;
import com.yunho.shop.dto.product.ProductDetailResponse;
import com.yunho.shop.dto.product.ProductSummaryResponse;
import com.yunho.shop.entity.product.Brand;
import com.yunho.shop.entity.product.Category;
import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductImage;
import com.yunho.shop.entity.product.ProductOption;
import com.yunho.shop.entity.product.SaleStatus;
import com.yunho.shop.global.exception.BusinessException;
import com.yunho.shop.global.exception.ErrorCode;
import com.yunho.shop.global.file.LocalImageStorage;
import com.yunho.shop.global.file.StoredImage;
import com.yunho.shop.repository.product.BrandRepository;
import com.yunho.shop.repository.product.CategoryRepository;
import com.yunho.shop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // 조회 메서드 기본값. 쓰기 메서드에만 @Transactional을 따로 붙임
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final LocalImageStorage imageStorage;

    /**
     * 상품 등록. 첫 번째 이미지가 대표 이미지가 된다.
     * 6단계에서 이 메서드 안에 outbox 저장을 추가해 이벤트의 출발점으로 쓴다.
     */
    @Transactional
    public Long createProduct(ProductCreateRequest request, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new BusinessException(ErrorCode.IMAGE_REQUIRED);
        }
        validateNoDuplicateOptions(request.options());

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        Brand brand = findBrandOrNull(request.brandId());

        Product product = Product.builder()
                .category(category)
                .brand(brand)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();

        for (int i = 0; i < images.size(); i++) {
            StoredImage stored = imageStorage.store(images.get(i));
            product.addImage(ProductImage.builder()
                    .imageUrl(stored.url())
                    .imageHash(stored.hash())
                    .isMain(i == 0)
                    .sortOrder(i)
                    .build());
        }

        for (OptionRequest option : request.options()) {
            product.addOption(ProductOption.builder()
                    .color(option.color())
                    .size(option.size())
                    .extraPrice(option.extraPrice())
                    .stock(option.stock())
                    .build());
        }

        // cascade = ALL이라 product만 저장해도 이미지와 옵션이 같이 INSERT됨
        return productRepository.save(product).getProductId();
    }

    public PageResponse<ProductSummaryResponse> getProducts(Long categoryId, Pageable pageable) {
        return PageResponse.of(
                productRepository.findDisplayProducts(categoryId, pageable)
                        .map(ProductSummaryResponse::from)
        );
    }

    public ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findDetailById(productId)
                .filter(p -> p.getSaleStatus() != SaleStatus.HIDDEN)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponse.from(product);
    }

    private Brand findBrandOrNull(Long brandId) {
        if (brandId == null) {
            return null;
        }
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }

    /** DB의 UNIQUE(product_id, color, size)에 걸리기 전에 먼저 막아서 알기 쉬운 에러를 돌려줌 */
    private void validateNoDuplicateOptions(List<OptionRequest> options) {
        Set<String> keys = new HashSet<>();
        for (OptionRequest option : options) {
            if (!keys.add(option.color() + "|" + option.size())) {
                throw new BusinessException(ErrorCode.DUPLICATE_OPTION);
            }
        }
    }
}
