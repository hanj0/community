package com.han.community.scheduler;

import com.han.community.service.BufferedViewCountIncrementer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "view-count.mode", havingValue = "buffered")
@RequiredArgsConstructor
@Slf4j
public class ViewCountScheduler {

    private final BufferedViewCountIncrementer incrementer;

    private static final long VIEW_COUNT_DELAY = 30 * 1_000L;

    @Scheduled(fixedDelay = VIEW_COUNT_DELAY)
    public void flush() {

        long start = System.currentTimeMillis();
        log.info("[flush] start");
        try {
            incrementer.flushToDb();
        } finally {
            log.info("[flush] end, 실행시간: {}ms", System.currentTimeMillis() - start);
        }
    }

}
