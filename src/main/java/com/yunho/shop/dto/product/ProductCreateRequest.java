package com.yunho.shop.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

/** 관리자 상품 등록 요청 (multipart의 "data" 파트에 JSON으로 들어옴) */
public record ProductCreateRequest(

        @NotNull(message = "카테고리를 선택해 주세요.")
        Long categoryId,

        /** 브랜드는 선택 */
        Long brandId,

        @NotBlank(message = "상품명을 입력해 주세요.")
        @Size(max = 200, message = "상품명은 200자 이하로 입력해 주세요.")
        String name,

        String description,

        @NotNull(message = "가격을 입력해 주세요.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        @NotEmpty(message = "옵션을 1개 이상 입력해 주세요.")
        List<@Valid OptionRequest> options
) {

    public record OptionRequest(

            @NotBlank(message = "색상을 입력해 주세요.")
            @Size(max = 30)
            String color,

            @NotBlank(message = "사이즈를 입력해 주세요.")
            @Size(max = 20)
            String size,

            @PositiveOrZero(message = "추가금은 0원 이상이어야 합니다.")
            int extraPrice,

            @PositiveOrZero(message = "재고는 0개 이상이어야 합니다.")
            int stock
    ) {
    }
}
