package com.han.community.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String LOGIN_LOCK_PREFIX = "login:lock:";
    private static final int LOGIN_LOCK_DURATION = 600;

    public void set(String key, String value, int duration) {
        setWithPrefix(key, value, duration, LOGIN_FAIL_PREFIX);
    }

    public void increment(String key) {

        Long value = redisTemplate.opsForValue().increment(LOGIN_FAIL_PREFIX + key);
        if(value != null && value >= 10) setLock(key);
    }

    public void reset(String key) {
        delete(key);
    }

    /**
     * 해당 아이디가 로그인 제한에 걸려있는지 확인한다.
     * 모종의 이유로 hasKey()가 null을 반환하게 된다면 NullPointerException 발생함을 알아둘것
     *
     * @param key   로그인 아이디
     * @return      로그인 제한 여부
     */
    public boolean isLocked(String key) {
        return redisTemplate.hasKey(LOGIN_LOCK_PREFIX + key) ;
    }

    private void setWithPrefix(String key, String value, int duration, String prefix) {
        redisTemplate.opsForValue().set(prefix + key, value, Duration.ofSeconds(duration));
    }

    private void setLock(String key) {
        setWithPrefix(key, "1", LOGIN_LOCK_DURATION, LOGIN_LOCK_PREFIX);
    }

    private void delete(String key) {
        redisTemplate.delete(LOGIN_FAIL_PREFIX + key);
    }
}
