package com.han.community.service;

import com.han.community.common.IntegrationTestSupport;
import com.han.community.dto.PostDto;
import com.han.community.entity.Channel;
import com.han.community.entity.Post;
import com.han.community.entity.Role;
import com.han.community.entity.User;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PostSoftDeleteTest extends IntegrationTestSupport {

    @Autowired PostRepository postRepository;
    @Autowired PostService postService;
    @Autowired ChannelRepository channelRepository;
    @Autowired UserRepository userRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    private Channel channel;
    private User user;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(
                Channel.builder().name("1").build()
        );
        user = userRepository.save(
                User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build()
        );
    }

    @Test
    void 삭제_후_조회() {

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
}
