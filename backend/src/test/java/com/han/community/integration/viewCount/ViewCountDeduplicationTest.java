package com.han.community.integration.viewCount;

import com.han.community.channel.entity.Channel;
import com.han.community.channel.repository.ChannelRepository;
import com.han.community.common.ConcurrentExecutor;
import com.han.community.common.IntegrationTestSupport;
import com.han.community.post.entity.Post;
import com.han.community.post.repository.PostRepository;
import com.han.community.user.entity.Role;
import com.han.community.user.entity.User;
import com.han.community.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
public class ViewCountDeduplicationTest extends IntegrationTestSupport {

    @Autowired MockMvc mockMvc;
    @Autowired ChannelRepository channelRepository;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired StringRedisTemplate redis;
    @Autowired RedisSessionRepository sessionRepository;

    User user;
    Post post;
    Post post2;

    @BeforeEach
    void setUp() {
        Channel channel = channelRepository.save(Channel.builder().name("1").build());
        user = userRepository.save(User.builder().username("username").email("email@email.com").password("password").role(Role.USER).build());
        post = postRepository.save(Post.builder().channel(channel).user(user).title("test_title").content("test_content").build());
        post2 = postRepository.save(Post.builder().channel(channel).user(user).title("test_title2").content("test_content2").build());
    }

    @AfterEach
    void tearDown() {
        redis.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        channelRepository.deleteAllInBatch();
    }

    @Test
    void 로그인_유저_조회시_조회수_1_증가() throws Exception {

        Long postId = post.getId();

        Cookie sessionCookie = createSessionCookie(sessionRepository, user);

        mockMvc.perform(
                get("/api/posts/{id}", postId)
                        .cookie(sessionCookie)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        System.out.println("-----------------------------------");
        System.out.println(redis.keys("*"));
        System.out.println("-----------------------------------");

        assertThat(redis.hasKey("post:" + postId + ":view:u:" + user.getId())).isTrue();
        assertThat(redis.opsForValue().get("post:" + postId + ":viewCount")).isEqualTo("1");
    }

    @Test
    void 로그인_유저_여러_게시글_조회시_각각_조회수_1_증가() throws Exception {

        Long postId = post.getId();
        Long postId2 = post2.getId();
        Cookie sessionCookie = createSessionCookie(sessionRepository, user);

        mockMvc.perform(get("/api/posts/{id}", postId).cookie(sessionCookie))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(get("/api/posts/{id}", postId2).cookie(sessionCookie))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(redis.hasKey("post:" + postId + ":view:u:" + user.getId())).isTrue();
        assertThat(redis.hasKey("post:" + postId2 + ":view:u:" + user.getId())).isTrue();
        assertThat(redis.opsForValue().get("post:" + postId + ":viewCount")).isEqualTo("1");
        assertThat(redis.opsForValue().get("post:" + postId2 + ":viewCount")).isEqualTo("1");
    }

    @Test
    void 로그인_유저_연속조회시_조회수_1_증가() throws Exception {

        Long postId = post.getId();
        Cookie sessionCookie = createSessionCookie(sessionRepository, user);

        for(int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/posts/{id}", postId).cookie(sessionCookie))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        assertThat(redis.hasKey("post:" + postId + ":view:u:" + user.getId())).isTrue();
        assertThat(redis.opsForValue().get("post:" + postId + ":viewCount")).isEqualTo("1");
    }

    @Test
    void 로그인_유저_ttl_만료후_다시_조회하면_조회수_한번_더_증가() throws Exception {

        Long postId = post.getId();
        Cookie cookie = createSessionCookie(sessionRepository, user);

        mockMvc.perform(get("/api/posts/{id}", postId).cookie(cookie))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(redis.getExpire(depKey(postId, user.getId()))).isCloseTo(24*60*60L, Offset.offset(60L));
        assertThat(redis.opsForValue().get(vcKey(postId))).isEqualTo("1");

        redis.delete(depKey(postId, user.getId()));

        mockMvc.perform(get("/api/posts/{id}", postId).cookie(cookie))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(redis.opsForValue().get(vcKey(postId))).isEqualTo("2");
    }

    @Test
    void 비로그인_쿠키가_없다면_쿠키발급_조회수는_증가하지않는다() throws Exception {

        Long postId = post.getId();

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(redis.opsForValue().get(vcKey(postId))).isNull();
    }

    @Test
    void 비로그인_발급된_쿠키로_여러번_요청시_조회수_1_증가() throws Exception {

        Long postId = post.getId();

        // 쿠키발급용
        MvcResult result = mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("vid");

        for(int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/posts/{id}", postId).cookie(cookie))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        assertThat(redis.opsForValue().get(vcKey(postId))).isEqualTo("1");
    }

    @Test
    void 비로그인_변조된_쿠키로_조회시_조회수는_증가하지않는다() throws Exception{

        Long postId = post.getId();

        mockMvc.perform(get("/api/posts/{id}", postId).cookie(new Cookie("vid", "not-a-uuid")))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertThat(redis.opsForValue().get(vcKey(postId))).isNull();
    }

    @Test
    void 로그인_유저_동시조회요청시_조회수_1_증가() throws Exception {

        int threads = 100;
        Long postId = post.getId();

        Cookie sessionCookie = createSessionCookie(sessionRepository, user);

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(threads, () -> {

            try {
                mockMvc.perform(get("/api/posts/{id}", postId).cookie(sessionCookie))
                        .andExpect(MockMvcResultMatchers.status().isOk());
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertThat(result.success()).isEqualTo(threads);
        assertThat(redis.hasKey(depKey(postId, user.getId()))).isTrue();
        assertThat(redis.opsForValue().get(vcKey(postId))).isEqualTo("1");
    }

    @Test
    void 비로그인_동시요청시_조회수_1_증가() throws Exception {

        int threads = 100;
        Long postId = post.getId();

        // 쿠키생성
        MvcResult mvcResult = mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Cookie cookie = mvcResult.getResponse().getCookie("vid");

        ConcurrentExecutor.Result result = ConcurrentExecutor.run(threads, () -> {

            try {
                mockMvc.perform(get("/api/posts/{id}", postId).cookie(cookie))
                        .andExpect(MockMvcResultMatchers.status().isOk());
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        });

        String guestCookie = cookie != null ? cookie.getValue() : "";

        assertThat(result.success()).isEqualTo(threads);
        assertThat(redis.hasKey("post:" + postId + ":g:" + guestCookie));
        assertThat(redis.opsForValue().get(vcKey(postId))).isEqualTo("1");
    }

    private Cookie createSessionCookie(SessionRepository<?> repo, User user) {

        return new Cookie(
                "SESSION",
                Base64.getEncoder().encodeToString(createLoginSession(repo, createContext(user)).getBytes(StandardCharsets.UTF_8))
        );
    }

    private SecurityContext createContext(User user) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        return context;
    }

    private <S extends Session> String createLoginSession(SessionRepository<S> repo, SecurityContext context) {

        S session = repo.createSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        repo.save(session);
        return session.getId();
    }

    private String depKey(Long postId, Long userId) {
        return "post:" + postId + ":view:u:" + userId;
    }

    private String vcKey(Long postId) {
        return "post:" + postId + ":viewCount";
    }
}
