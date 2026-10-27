package com.han.community.scheduler;

import com.han.community.service.BufferedViewCountIncrementer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "view-count.mode", havingValue = "buffered")
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final BufferedViewCountIncrementer incrementer;

    private static final long VIEW_COUNT_DELAY = 10 * 60 * 1_000L;

    @Scheduled(fixedDelay = VIEW_COUNT_DELAY)
    public void flush() {
        incrementer.flushToDb();
    }

}
