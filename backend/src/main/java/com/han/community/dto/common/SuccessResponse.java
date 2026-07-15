package com.han.community.dto.common;

import java.time.Instant;

public record SuccessResponse<T>(T data, Meta meta) {

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data, new Meta(Instant.now()));
    }

    public record Meta(Instant timestamp) {}
}
