package com.han.community.service;

import com.han.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name="view-count.mode", havingValue = "buffered")
@RequiredArgsConstructor
public class BufferedViewCountIncrementer implements ViewCountIncrementer {

    private final PostRepository postRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String VIEW_COUNT_PREFIX = "post:viewCount:";
    private static final String VIEW_COUNT_DIRTY_SET_KEY = "post:viewCount:dirty";
    private static final int BATCH_SIZE = 500;

    @Override
    public void increment(Long postId) {

        redisTemplate.opsForSet().add(VIEW_COUNT_DIRTY_SET_KEY, Long.toString(postId));
        redisTemplate.opsForValue().increment(VIEW_COUNT_PREFIX + postId);
    }

    /**
     * redis set에서 key를 뽑고 값을 가져와 조회수를 반영한다.
     * 값을 가져와 db에 반영하는 구간에서 데이터 손실이 날 수 있음을 고려해야한다.
     *
     * 발생가능한 예외
     * redis 장애
     * NumberFormatException - value를 parse하는 과정에서 혹시 숫자대신 다른값일 경우
     */
    public void flushToDb() {

        List<String> failed = new ArrayList<>();

        while(true) {

            List<String> members = redisTemplate.opsForSet().pop(VIEW_COUNT_DIRTY_SET_KEY, BATCH_SIZE);
            if(members == null || members.isEmpty()) break;

            for(String member : members) {

                String value = redisTemplate.opsForValue().getAndDelete(VIEW_COUNT_PREFIX + member);
                if (value == null) {
                    log.debug("dirty set에는 있는데 조회수key 없음: id={}", member);
                    continue;
                }
                long viewCount = Long.parseLong(value);

                try {
                    postRepository.flushViewCounts(viewCount, Long.valueOf(member));
                } catch(DataAccessException e) {
                    redisTemplate.opsForValue().increment(VIEW_COUNT_PREFIX + member, viewCount);
                    failed.add(member);
                    log.error("조회수 반영 실패 id={}, count={}", member, viewCount, e);
                }
            }
        }

        if(!failed.isEmpty()) {
            redisTemplate.opsForSet().add(VIEW_COUNT_DIRTY_SET_KEY, failed.toArray(String[]::new));
        }
    }
}
