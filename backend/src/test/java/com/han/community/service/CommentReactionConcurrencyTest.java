package com.han.community.service;

import com.han.community.common.ConcurrencyTestSupport;
import com.han.community.dto.CommentReactionDto;
import com.han.community.entity.*;
import com.han.community.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentReactionConcurrencyTest extends ConcurrencyTestSupport {

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
        channel = channelRepository.save(
                Channel.builder().name("1").build()
        );
        user = userRepository.save(
                User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build()
        );
        post = postRepository.save(
                Post.builder().channel(channel).user(user).title("title").content("content").build()
        );
        comment = commentRepository.save(
                Comment.builder().user(user).post(post).content("content").build()
        );
    }

    @AfterEach
    void tearDown() {
        commentReactionRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        channelRepository.deleteAllInBatch();
    }

    @Test
    void 동일한_댓글_리액션_요청() throws InterruptedException {


        int threadCount = 10;
        CommentReactionDto.Request dto = new CommentReactionDto.Request(ReactionType.LIKE);
        Long commentId = comment.getId();
        Long userId = user.getId();

        ConcurrentResult result = runConcurrently(
                threadCount,
                () -> commentReactionService.reactToComment(commentId, userId, dto)
        );

        assertThat(commentReactionRepository.count()).isEqualTo(1);
        assertThat(result.duplicate()).isZero();
        assertThat(result.otherFail()).isZero();
    }
}
