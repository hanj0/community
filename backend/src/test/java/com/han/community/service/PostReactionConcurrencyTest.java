package com.han.community.service;

import com.han.community.common.ConcurrencyTestSupport;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class PostReactionConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired PostReactionRepository postReactionRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired PostReactionService postReactionService;

    private User user;
    private Channel channel;
    private Post post;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(
                Channel.builder().name("1").build()
        );
        user = userRepository.save(
                User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build()
        );
        post = postRepository.save(
                Post.builder().channel(channel).user(user).title("title").content("content").build()
        );
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

        ConcurrentResult result = runConcurrently(
                threadCount,
                () -> postReactionService.reactToPost(postId, userId, requestDto)
        );

        assertThat(postReactionRepository.count()).isEqualTo(1);
        assertThat(result.duplicate()).isZero();
        assertThat(result.otherFail()).isZero();
    }

    @Test
    void 같은_리액션_동시삭제() throws InterruptedException {

        Long postId = post.getId();
        Long userId = user.getId();
        PostReactionDto.Request request = new PostReactionDto.Request(ReactionType.LIKE);
        postReactionService.reactToPost(postId, userId, request);
Post init = postRepository.findById(postId).orElseThrow();
        System.out.println(init.getLikeCount());

        int threadCount = 10;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        try(ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            for(int i = 0; i < threadCount; i ++) {
                executor.submit(() -> {
                    try {
                        start.await();
                        postReactionService.deleteReaction(postId, userId);
                    } catch(Throwable t) {
                        t.printStackTrace();
                    } finally {
                        done.countDown();
                    }
                });
            }
            start.countDown();
            done.await();
        }
        Post post = postRepository.findById(postId).orElseThrow();
System.out.println(post.getLikeCount());
        assertThat(post.getLikeCount()).isEqualTo(0);
        assertThat(postReactionRepository.findByPostIdAndUserId(postId, userId)).isEmpty();
    }
}
