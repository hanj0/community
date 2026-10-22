package com.han.community.integration.concurrency;

import com.han.community.common.ConcurrentExecutor;
import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.CommentReactionDto;
import com.han.community.entity.*;
import com.han.community.repository.*;
import com.han.community.service.CommentReactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentReactionConcurrencyTest extends IntegrationTestSupport {

    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentReactionService commentReactionService;
    @Autowired CommentReactionRepository commentReactionRepository;

    private User user;
    private Channel channel;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(Channel.builder().name("1").build());
        user = userRepository.save(User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build());
        post = postRepository.save(Post.builder().channel(channel).user(user).title("title").content("content").build());
        comment = commentRepository.save(Comment.builder().user(user).post(post).content("content").build());
    }

    @AfterEach
    void tearDown() {
        commentReactionRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        channelRepository.deleteAllInBatch();
    }

    /**
     * advisory락으로 동시요청을 막음
     */
    @Test
    void 동일한_댓글_리액션_요청() throws InterruptedException {

        int threadCount = 10;
        CommentReactionDto.Request dto = new CommentReactionDto.Request(ReactionType.LIKE);
        Long commentId = comment.getId();
        Long userId = user.getId();

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(
                threadCount,
                () -> commentReactionService.reactToComment(commentId, userId, dto)
        );

        assertThat(commentRepository.findById(commentId).orElseThrow().getLikeCount()).isEqualTo(1);
        assertThat(commentReactionRepository.count()).isEqualTo(1);
        assertThat(result.success()).isEqualTo(threadCount);
        assertThat(result.failures()).isEmpty();
    }

}
