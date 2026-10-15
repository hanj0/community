package com.han.community.integration.auth;

import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.AuthDto;
import com.han.community.entity.Role;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.UserRepository;
import com.han.community.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LoginFailureLimitTest extends IntegrationTestSupport {

    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.save(new User("test", "test@test.test", passwordEncoder.encode("test"), Role.USER));

        Set<String> keys = redisTemplate.keys("login:*");
        redisTemplate.delete(keys);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    void 로그인을_기준치_이상_실패시_로그인이_제한된다() {

        int failureThreshold = 10;

        // 틀린 비밀번호로 로그인
        for(int i = 0; i < failureThreshold; i++) {

            assertThatThrownBy(() -> authService.login(new AuthDto.LoginRequest("test@test.test", "wrongPW")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_LOGIN);
        }

        // 올바른 비밀번호로 로그인
        assertThatThrownBy(() -> authService.login(new AuthDto.LoginRequest("test@test.test", "test")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.LOGIN_LOCKED);

    }

    @Test
    void 기준치_미만_실패시_로그인_가능() {

        int count = 5;

        for(int i = 0; i < count; i++) {

            assertThatThrownBy(() -> authService.login(new AuthDto.LoginRequest("test@test.test", "wrongPW")))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_LOGIN);
        }

        Authentication auth = authService.login(new AuthDto.LoginRequest("test@test.test", "test"));

        assertThat(((User)auth.getPrincipal()).getUsername()).isEqualTo("test");
    }

}