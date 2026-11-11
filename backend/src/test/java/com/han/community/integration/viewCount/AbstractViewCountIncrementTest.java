package com.han.community.integration.viewCount;

import com.han.community.common.ConcurrentExecutor;
import com.han.community.common.IntegrationTestSupport;
import com.han.community.channel.entity.Channel;
import com.han.community.post.entity.Post;
import com.han.community.user.entity.Role;
import com.han.community.user.entity.User;
import com.han.community.channel.repository.ChannelRepository;
import com.han.community.post.repository.PostRepository;
import com.han.community.user.repository.UserRepository;
import com.han.community.post.viewcount.ViewCountIncrementer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractViewCountIncrementTest extends IntegrationTestSupport {

    @Autowired protected ViewCountIncrementer incrementer;
    @Autowired protected PostRepository postRepository;
    @Autowired protected ChannelRepository channelRepository;
    @Autowired protected UserRepository userRepository;
    @Autowired protected JdbcTemplate jdbcTemplate;
    @Autowired protected StringRedisTemplate redisTemplate;

    protected Channel channel;
    protected User user;
    protected Long postId;

    @BeforeEach
    void setUp() {
        channel = channelRepository.save(Channel.builder().name("1").build());
        user = userRepository.save(User.builder().username("username1").email("email1@email.com").password("password").role(Role.USER).build());
        postId = postRepository.save(Post.builder().channel(channel).user(user).title("test_title").content("test_content").build()).getId();
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.batchUpdate("DELETE FROM comment", "DELETE FROM post", "DELETE FROM users", "DELETE FROM channel");
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    protected void SyncToDb() {}

    protected long viewCountOf(Long id) {
        return postRepository.findById(id).orElseThrow().getViewCount();
    }

    @Test
    void 순차_100번_조회시_조회수_100_증가() {

        int count = 100;

        for(int i = 0; i < count; i++) incrementer.increment(postId);
        SyncToDb();
        assertThat(viewCountOf(postId)).isEqualTo(count);
    }

    @Test
    void 동시_N쓰레드xM번_조회시_조회수_NxM_증가() throws InterruptedException {

        int count = 1;
        int threadCount = 5000;

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(
                threadCount,
                () -> {
                    for(int i = 0; i < count; i++) incrementer.increment(postId);
                    SyncToDb();
                }
        );

        assertThat(viewCountOf(postId)).isEqualTo(count * threadCount);
    }
}
