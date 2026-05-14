package com.han.community.service;


import com.han.community.dto.PostDto;
import com.han.community.entity.Channel;
import com.han.community.entity.Post;
import com.han.community.entity.User;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostDto.Response create(PostDto.CreateRequest requestDto, Long userId) {

        Channel channel = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("채널 없음"));
        User user = userRepository.getReferenceById(userId);

        Post post = new Post.Builder()
                .channel(channel)
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
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
