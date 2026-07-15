package com.han.community.dto.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record NotificationCursor(
        LocalDateTime updatedAt,
        Long id
) {

    private static final ZoneOffset OFFSET = ZoneOffset.of("+09:00");

    public static NotificationCursor parse(String encoded) {

        if (encoded == null || encoded.isBlank()) return null;

        String[] arr = encoded.split("_");
        return new NotificationCursor(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(arr[0])), OFFSET),
                Long.parseLong(arr[1])
        );
    }

    public String encode() {
        return updatedAt.toInstant(OFFSET).toEpochMilli() + "_" + id;
    }
}
