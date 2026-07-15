package com.han.community.dto.common;

import java.util.List;

public record CursorResponse<T>(
        List<T> data,
        String nextCursor,
        boolean hasNext
) {
    public static <T> CursorResponse<T> of(List<T> data, String nextCursor, boolean hasNext) {
        return new CursorResponse<>(data, nextCursor, hasNext);
    }
}
