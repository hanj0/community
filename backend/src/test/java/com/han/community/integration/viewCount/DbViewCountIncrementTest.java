package com.han.community.integration.viewCount;

import com.han.community.service.DbViewCountIncrementer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = "view-count.mode=db")
public class DbViewCountIncrementTest extends AbstractViewCountIncrementTest {

    @Test
    void 구현체_확인() {
        assertThat(incrementer).isInstanceOf(DbViewCountIncrementer.class);
    }
}
