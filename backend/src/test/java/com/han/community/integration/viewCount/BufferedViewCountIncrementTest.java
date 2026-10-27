package com.han.community.integration.viewCount;

import com.han.community.service.BufferedViewCountIncrementer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "view-count.mode=buffered")
public class BufferedViewCountIncrementTest extends AbstractViewCountIncrementTest {

    @Autowired BufferedViewCountIncrementer buffered;

    @Override
    protected void SyncToDb() {

//        try(Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("post:viewCount:*").build())) {
//            while(cursor.hasNext()) {
//                String key = cursor.next();
//
//                System.out.println(key + " " );
//            }
//        }
        buffered.flushToDb();
    }

    @Test
    void 구현체_확인() {
        assertThat(incrementer).isInstanceOf(BufferedViewCountIncrementer.class);
    }
}
