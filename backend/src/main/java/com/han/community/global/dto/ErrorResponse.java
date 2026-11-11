package com.han.community.global.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(Error error) {

    public record Error(
            String code,
            String message,
            List<FieldError> details,
            String requestId,
            Instant timestamp
    ) {}

    public record FieldError(String field, String reason) {}
}
