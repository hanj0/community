package com.han.community.dto;

import com.han.community.dto.common.NotificationCursor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationCursorTest {

    @Test
    void 커서_시간_인코딩_디코딩() {

        LocalDateTime origin = LocalDateTime.parse("2020-01-01T12:12:12.123456");
        Long id = 100L;

        NotificationCursor cursor = new NotificationCursor(origin, id);
        NotificationCursor decoded = NotificationCursor.parse(cursor.encode());

        assertThat(decoded.updatedAt()).isEqualTo(origin);
        assertThat(decoded.id()).isEqualTo(id);
    }
}
