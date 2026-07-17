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

        int idx = encoded.lastIndexOf("_");
        return new NotificationCursor(
                LocalDateTime.ofInstant(ofEpochSecond(encoded.substring(0, idx)), OFFSET),
                Long.parseLong(encoded.substring(idx + 1))
        );
    }

    public String encode() {
        return toEpochMicro(updatedAt.toInstant(OFFSET)) + "_" + id;
    }

    private static Instant ofEpochSecond(String timeString) {

        long epochMicro = Long.parseLong(timeString);
        long secs = Math.floorDiv(epochMicro, 1000000);
        long mos = Math.floorMod(epochMicro, 1000000);
        return Instant.ofEpochSecond(secs, mos * 1000);
    }

    private long toEpochMicro(Instant instant) {

        long seconds = instant.getEpochSecond();
        int nanos = instant.getNano();

        if (seconds < 0L && nanos > 0) {
            long micros = Math.multiplyExact(seconds + 1L, 1000000);
            long adjustment = (long)(nanos / 1000 - 1000000);
            return Math.addExact(micros, adjustment);
        } else {
            long micros = Math.multiplyExact(seconds, 1000000);
            return Math.addExact(micros, (long)(nanos / 1000));
        }
    }
}
