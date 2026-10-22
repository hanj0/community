package com.han.community.integration.concurrency;

import com.han.community.common.ConcurrentExecutor;
import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.PostReactionDto;
import com.han.community.entity.*;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostReactionRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import com.han.community.service.PostReactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class PostReactionConcurrencyTest extends IntegrationTestSupport {

    @Autowired PostReactionRepository postReactionRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired PostReactionService postReactionService;

    private Channel channel;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(Channel.builder().name("1").build());
        user = userRepository.save(User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build());
        post = postRepository.save(Post.builder().channel(channel).user(user).title("title").content("content").build());
    }

    @AfterEach
    void tearDown() {
        postReactionRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        channelRepository.deleteAll();
    }

    @Test
    void 같은_리액션_동시요청() throws InterruptedException {

        int threadCount = 10;
        Long postId = post.getId();
        Long userId = user.getId();
        PostReactionDto.Request requestDto = new PostReactionDto.Request(ReactionType.DISLIKE);

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(
                threadCount,
                () -> postReactionService.reactToPost(postId, userId, requestDto)
        );

        assertThat(postRepository.findById(postId).orElseThrow().getDislikeCount()).isEqualTo(1);
        assertThat(postReactionRepository.count()).isEqualTo(1);
        assertThat(result.success()).isEqualTo(threadCount);
        assertThat(result.failures()).isEmpty();
    }

    @Test
    void 같은_리액션_동시삭제() throws InterruptedException {

        int threadCount = 10;
        Long postId = post.getId();
        Long userId = user.getId();
        PostReactionDto.Request request = new PostReactionDto.Request(ReactionType.LIKE);
        postReactionService.reactToPost(postId, userId, request);
        assertThat(postRepository.findById(postId).orElseThrow().getLikeCount()).isEqualTo(1);

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(
                threadCount,
                () -> postReactionService.deleteReaction(postId, userId)
        );

        assertThat(postRepository.findById(postId).orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(result.success()).isEqualTo(threadCount);
        assertThat(result.failures()).isEmpty();
        assertThat(postReactionRepository.findByPostIdAndUserId(postId, userId)).isEmpty();
    }
}
