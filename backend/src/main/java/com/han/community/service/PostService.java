package com.han.community.service;


import com.han.community.dto.PostDto;
import com.han.community.entity.Post;
import com.han.community.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostDto.Response create(PostDto.CreateRequest dto) {

        // dto를 받아서 entity 인스턴스 만들고, repo 저장 후 id + 필드 반환
        Post post = new Post.Builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        Post savePost = postRepository.save(post);

        return PostDto.Response.from(post);
    }

    @Transactional
    public PostDto.Response getDetail(Long id) {

        Post post = postRepository.findById(id).orElseThrow();
        return PostDto.Response.from(post);
    }

    @Transactional
    public PostDto.Response update(PostDto.UpdateRequest dto) {

        Post post = postRepository.findById(dto.getId()).orElseThrow();
        post.update(dto.getTitle(), dto.getContent());

        return PostDto.Response.from(post);
    }

    @Transactional
    public void delete(Long id) {

        // soft delete로 수정 필요
        postRepository.deleteById(id);
    }

    @Transactional
    public Page<PostDto.Response> getPostPage(Pageable pageable) {

        Page<Post> page = postRepository.findAll(pageable);
        return page.map(p -> PostDto.Response.from(p));
    }
}
