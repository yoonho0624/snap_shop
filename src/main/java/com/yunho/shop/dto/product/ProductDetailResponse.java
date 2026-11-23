package com.yunho.shop.dto.product;

import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductImage;
import com.yunho.shop.entity.product.ProductOption;
import com.yunho.shop.entity.product.SaleStatus;

import java.util.List;

public record ProductDetailResponse(
        Long productId,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        String name,
        String description,
        int price,
        SaleStatus saleStatus,
        List<ImageResponse> images,
        List<OptionResponse> options
) {

    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
                product.getProductId(),
                product.getCategory().getCategoryId(),
                product.getCategory().getName(),
                product.getBrand() != null ? product.getBrand().getBrandId() : null,
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSaleStatus(),
                product.getImages().stream().map(ImageResponse::from).toList(),
                product.getOptions().stream().map(OptionResponse::from).toList()
        );
    }

    public record ImageResponse(Long imageId, String imageUrl, boolean main, int sortOrder) {
        static ImageResponse from(ProductImage image) {
            return new ImageResponse(image.getProductImageId(), image.getImageUrl(), image.isMain(), image.getSortOrder());
        }
    }

    public record OptionResponse(
            Long optionId,
            String color,
            String size,
            int extraPrice,
            int unitPrice,
            int stock,
            boolean soldOut
    ) {
        static OptionResponse from(ProductOption option) {
            return new OptionResponse(
                    option.getProductOptionId(),
                    option.getColor(),
                    option.getSize(),
                    option.getExtraPrice(),
                    option.getUnitPrice(),
                    option.getStock(),
                    option.getStock() == 0
            );
        }
    }
}
