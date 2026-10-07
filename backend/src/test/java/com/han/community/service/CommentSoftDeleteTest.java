package com.han.community.service;

import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.CommentDto;
import com.han.community.entity.*;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentSoftDeleteTest extends IntegrationTestSupport {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private Channel channel;
    private User user;
    private User postUser;
    private Post post;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(
                Channel.builder().name("1").build()
        );
        postUser = userRepository.save(
                User.builder().username("username1").email("email@email.com").password("password").role(Role.USER).build()
        );
        user = userRepository.save(
                User.builder().username("username2").email("email2@email.com").password("password").role(Role.USER).build()
        );
        post = postRepository.save(
                Post.builder().channel(channel).user(postUser).title("test_title").content("test_content").build()
        );
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.batchUpdate(
                "DELETE FROM Comment",
                "DELETE FROM Post",
                "DELETE FROM Users",
                "DELETE FROM Channel"
        );
    }

    @Test
    void 댓글_삭제하면_목록에서_제외() {

        // 댓글 생성
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content("test_content")
                .build();
        commentRepository.save(comment);
        Long id = comment.getId();
        assertThat(commentRepository.findById(id)).isNotEmpty();

        // 댓글 삭제
        commentService.delete(id, user.getId());
        assertThat(commentService.getComments(post.getId(), user.getId(), PageRequest.of(0, 10))).isEmpty();
    }

    @Test
    void 대댓글_삭제하면_목록에서_제외() {

        // 댓글 & 대댓글 생성
        User replyUser = userRepository.save(
                User.builder().username("username3").email("email3@email.com").password("password").role(Role.USER).build()
        );

        Comment parent = Comment.builder()
                .post(post)
                .user(user)
                .content("test_comment")
                .build();
        commentRepository.save(parent);

        Comment reply = Comment.builder()
                .post(post)
                .user(replyUser)
                .parentComment(parent)
                .content("test_reply")
                .build();
        commentRepository.save(reply);

        Page<Comment> repliesBeforeDelete = commentRepository.findByParentCommentId(parent.getId(), PageRequest.of(0, 10));
        assertThat(repliesBeforeDelete).isNotEmpty();

        // 대댓글 삭제
        commentService.delete(reply.getId(), replyUser.getId());

        Page<Comment> repliesAfterDelete = commentRepository.findByParentCommentId(parent.getId(), PageRequest.of(0, 10));
        assertThat(repliesAfterDelete).isEmpty();
        assertThat(commentRepository.findByPostIdAndParentCommentIsNull(post.getId(), PageRequest.of(0, 10))).hasSize(1);
    }

    @Test
    void 대댓글_존재할때_댓글_삭제하면_대댓글까지_전부_삭제() {

        // 댓글 & 대댓글 생성
        User replyUser = userRepository.save(
                User.builder().username("username4").email("email4@email.com").password("password").role(Role.USER).build()
        );

        Long parentId = commentService.create(
                new CommentDto.CreateRequest(null, "test_parent"), post.getId(), user.getId()
        ).getId();

        Long replyId = commentService.create(
                new CommentDto.CreateRequest(parentId, "test_reply"), post.getId(), replyUser.getId()
        ).getId();

        // 생성 확인
        assertThat(commentRepository.findByPostIdAndParentCommentIsNull(post.getId(), PageRequest.of(0, 10))).hasSize(1);
        assertThat(commentRepository.findByParentCommentId(parentId, PageRequest.of(0, 10))).hasSize(1);

        // 댓글 삭제
        commentService.delete(parentId, user.getId());

        assertThat(commentRepository.findByPostIdAndParentCommentIsNull(post.getId(), PageRequest.of(0, 10))).isEmpty();
        assertThat(commentRepository.findByParentCommentId(parentId, PageRequest.of(0, 10))).isEmpty();
        assertThat(jdbcTemplate.queryForObject("SELECT deleted_at FROM Comment WHERE id = ?", LocalDateTime.class, replyId)).isNotNull();
    }
}
