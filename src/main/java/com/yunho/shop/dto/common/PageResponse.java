package com.yunho.shop.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Page 객체를 그대로 반환하면 내부 구조가 응답에 다 드러나고, Spring Data가 경고도 띄운다.
 * 프론트에 필요한 값만 담아서 보낸다.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
