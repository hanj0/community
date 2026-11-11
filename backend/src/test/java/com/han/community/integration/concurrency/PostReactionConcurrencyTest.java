package com.han.community.integration.concurrency;

import com.han.community.channel.entity.Channel;
import com.han.community.common.ConcurrentExecutor;
import com.han.community.common.IntegrationTestSupport;
import com.han.community.post.dto.PostReactionDto;
import com.han.community.post.entity.Post;
import com.han.community.post.entity.ReactionType;
import com.han.community.channel.repository.ChannelRepository;
import com.han.community.post.repository.PostReactionRepository;
import com.han.community.post.repository.PostRepository;
import com.han.community.user.repository.UserRepository;
import com.han.community.post.service.PostReactionService;
import com.han.community.user.entity.Role;
import com.han.community.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
