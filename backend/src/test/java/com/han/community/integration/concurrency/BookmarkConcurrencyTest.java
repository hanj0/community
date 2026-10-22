package com.han.community.integration.concurrency;

import com.han.community.common.ConcurrentExecutor;
import com.han.community.common.IntegrationTestSupport;
import com.han.community.entity.Channel;
import com.han.community.entity.Post;
import com.han.community.entity.Role;
import com.han.community.entity.User;
import com.han.community.repository.BookmarkRepository;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import com.han.community.service.BookmarkService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class BookmarkConcurrencyTest extends IntegrationTestSupport {

    @Autowired BookmarkService bookmarkService;
    @Autowired BookmarkRepository bookmarkRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired ChannelRepository channelRepository;

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
        bookmarkRepository.deleteAll();
    }

    /**
     * 북마크는 postId, userId 유니크제약이 있고 ON CONFLICT로 같은요청을 무시한다
     */
    @Test
    void 같은_북마크_동시요청() throws InterruptedException {

        int threadCount = 10;
        Long userId = user.getId();
        Long postId = post.getId();

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(
                threadCount,
                () -> bookmarkService.setBookmark(postId, userId)
        );

        assertThat(bookmarkRepository.count()).isEqualTo(1);
        assertThat(result.success()).isEqualTo(threadCount);
        assertThat(result.failures()).isEmpty();
    }
}
