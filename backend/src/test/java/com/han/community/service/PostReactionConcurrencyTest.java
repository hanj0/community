package com.han.community.service;

import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.PostReactionDto;
import com.han.community.entity.*;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostReactionRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class PostReactionConcurrencyTest extends IntegrationTestSupport {

    @Autowired PostReactionRepository postReactionRepository;
    @Autowired PostReactionService postReactionService;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        Channel channel = channelRepository.save(
                Channel.builder().name("1").build()
        );
        User user = userRepository.save(
                User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build()
        );
        postRepository.save(
                Post.builder().channel(channel).user(user).title("title").content("content").build()
        );
    }

    @Test
    void 같은_리액션_동시요청() throws InterruptedException {

        int threadCount = 10;
        Long postId = 1L;
        Long userId = 1L;
        PostReactionDto.Request requestDto = new PostReactionDto.Request(ReactionType.DISLIKE);

        ConcurrentResult result = runConcurrently(
                threadCount,
                () -> postReactionService.reactToPost(postId, userId, requestDto)
        );

        assertThat(postReactionRepository.count()).isEqualTo(1);
        //assertThat(result.duplicate()).isZero();
        //assertThat(result.otherFail()).isZero();
    }
}
