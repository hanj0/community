package com.han.community.service;

import com.han.community.repository.ViewCountJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name="view-count.mode", havingValue = "buffered")
@RequiredArgsConstructor
public class BufferedViewCountIncrementer implements ViewCountIncrementer {

    private final StringRedisTemplate redisTemplate;
    private final ViewCountJdbcRepository viewCountJdbcRepository;

    private static final String VIEW_COUNT_PREFIX = "post:viewCount:";
    private static final String DIRTY_SET_KEY = "post:dirty:viewCount";
    private static final int BATCH_SIZE = 500;

    @Override
    public void increment(Long postId) {

        redisTemplate.opsForSet().add(DIRTY_SET_KEY, postId.toString());
        redisTemplate.opsForValue().increment(VIEW_COUNT_PREFIX + postId);
    }

    public record ViewCountDelta(
            Long id,
            long delta
    ){}

    /**
     * drain -> flush -> 실패 시 restore
     */
    public void flushToDb() {

        Long size = redisTemplate.opsForSet().size(DIRTY_SET_KEY);
        if(size == null) return;
        List<String> failed = new ArrayList<>();

        try {
            while(size > 0) {

                List<ViewCountDelta> chunk = drain();
                if(chunk == null) break;

                size -= BATCH_SIZE;
                if(chunk.isEmpty()) continue;

                try {
                    flush(chunk);
                } catch(DataAccessException e) {
                    log.error("조회수 반영 실패", e);
                    restore(chunk);
                    chunk.forEach(data -> failed.add(data.id().toString()));
                }
            }
        } finally {
            if(!failed.isEmpty()) {
                redisTemplate.opsForSet().add(DIRTY_SET_KEY, failed.toArray(String[]::new));
            }
        }
    }

    private List<ViewCountDelta> drain() {

        List<String> members = redisTemplate.opsForSet().pop(DIRTY_SET_KEY, BATCH_SIZE);
        if(members == null || members.isEmpty()) return null;

        List<ViewCountDelta> chunk = new ArrayList<>(members.size());

        for(String member : members) {

            String value = redisTemplate.opsForValue().getAndDelete(VIEW_COUNT_PREFIX + member);
            if(value == null) {
                log.warn("dirty set에는 있으나 조회수key 없음: id={}", member);
                continue;
            }

            chunk.add(new ViewCountDelta(Long.valueOf(member), Long.parseLong(value)));
        }
        chunk.sort(Comparator.comparingLong(ViewCountDelta::id));
        return chunk;
    }

    private void flush(List<ViewCountDelta> chunk) {
        viewCountJdbcRepository.flushViewCountsBatch(chunk);
    }

    private void restore(List<ViewCountDelta> chunk) {

        for(ViewCountDelta c : chunk) {
            redisTemplate.opsForValue().increment(VIEW_COUNT_PREFIX + c.id(), c.delta());
        }
    }
}
