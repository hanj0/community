package com.han.community.service;

import com.han.community.common.ConcurrencyTestSupport;
import com.han.community.entity.Channel;
import com.han.community.entity.Post;
import com.han.community.entity.Role;
import com.han.community.entity.User;
import com.han.community.repository.BookmarkRepository;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class BookmarkConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired BookmarkService bookmarkService;
    @Autowired BookmarkRepository bookmarkRepository;
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

    @AfterEach
    void tearDown() {
        bookmarkRepository.deleteAll();
    }

    @Test
    void 같은_북마크_동시요청() throws InterruptedException {

        int threadCount = 10;
        Long userId = 1L;
        Long postId = 1L;

        ConcurrentResult result = runConcurrently(
                threadCount,
                () -> bookmarkService.setBookmark(postId, userId)
        );

        assertThat(bookmarkRepository.count()).isEqualTo(1);
        assertThat(result.duplicate()).isZero();
        assertThat(result.otherFail()).isZero();
    }
}
