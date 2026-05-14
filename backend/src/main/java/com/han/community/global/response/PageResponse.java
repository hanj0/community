package com.han.community.global.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<T> data, PageMeta meta) {

    public record PageMeta(
            int page,
            int size,
            long total,
            int totalPages
    ) {}

    public static <T> PageResponse<T> of(Page<T> page) {

        return new PageResponse<>(
                page.getContent(),
                new PageMeta(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }
}
