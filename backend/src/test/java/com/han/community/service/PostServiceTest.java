package com.han.community.service;

import com.han.community.dto.PostDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void 게시글_생성_테스트() {

        // given
        PostDto.CreateRequest request =
                new PostDto.CreateRequest();

        // when
        Long postId = postService.create(request).getId();

        // then
        assertThat(postId).isNotNull();
    }
}
