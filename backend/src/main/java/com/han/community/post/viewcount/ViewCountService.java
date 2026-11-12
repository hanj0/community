package com.han.community.post.viewcount;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private StringRedisTemplate redis;
    private static final Duration VIEW_COUNT_DEDUPLICATION_TTL = Duration.ofHours(24);

    public boolean setIfFirst(Long postId, String viewerId) {

        if(viewerId == null) return false;

        Boolean first = redis.opsForValue().setIfAbsent(setKey(postId, viewerId), "1", VIEW_COUNT_DEDUPLICATION_TTL);
        Objects.requireNonNull(first, "현재코드에서 setIfAbsent은 null을 반환하면 안됨");

        return first;
    }

    private String setKey(Long postId, String viewerId) {
        return "post:" + postId + ":view:" + viewerId;
    }
}
