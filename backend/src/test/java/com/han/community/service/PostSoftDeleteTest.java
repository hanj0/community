package com.han.community.service;

import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.PostDto;
import com.han.community.entity.*;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PostSoftDeleteTest extends IntegrationTestSupport {

    @Autowired PostRepository postRepository;
    @Autowired PostService postService;
    @Autowired ChannelRepository channelRepository;
    @Autowired UserRepository userRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    private Channel channel;
    private User user;
    private Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(
                Channel.builder().name("1").build()
        );
        user = userRepository.save(
                User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build()
        );
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.batchUpdate(
                "DELETE FROM comment",
                "DELETE FROM post",
                "DELETE FROM users",
                "DELETE FROM channel"
        );
    }

    @Test
    void 삭제하면_조회목록에서_제외된다() {

        Post post = Post.builder()
                .channel(channel)
                .user(user)
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);
        Long id = post.getId();

        assertThat(postRepository.findById(id)).isNotEmpty();

        postService.delete(1L, 1L);
        assertThat(postRepository.findById(id)).isEmpty();

        LocalDateTime deletedAt = jdbcTemplate.queryForObject(
                "SELECT deleted_at FROM Post WHERE id = ?", LocalDateTime.class, id
        );
        assertThat(deletedAt).isNotNull();
    }

    @Test
    void 게시글을_삭제하면_댓글도_삭제된다() {

        // 게시글 생성, 댓글 & 대댓글 생성
        Post post = Post.builder().channel(channel).user(user).title("title").content("content").build();
        postRepository.save(post);
        Comment parent = Comment.builder().post(post).user(user).content("test_comment").build();
        commentRepository.save(parent);
        Comment reply = Comment.builder().post(post).user(user).parentComment(parent).content("test_reply").build();
        commentRepository.save(reply);

        // 생성 확인
        assertThat(postRepository.findAllWithChannelAndUser(pageable)).hasSize(1);
        assertThat(commentRepository.findByPostIdAndParentCommentIsNull(post.getId(), pageable)).hasSize(1);
        assertThat(commentRepository.findByParentCommentId(parent.getId(), pageable)).hasSize(1);

        // 게시글 삭제
        postService.delete(post.getId(), user.getId());

        // 삭제 확인
        assertThat(postRepository.findAllWithChannelAndUser(pageable)).isEmpty();
        assertThat(commentRepository.findByPostIdAndParentCommentIsNull(post.getId(), pageable)).isEmpty();
        assertThat(commentRepository.findByParentCommentId(parent.getId(), pageable)).isEmpty();
    }
}
