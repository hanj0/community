package com.han.community.service;

import com.han.community.dto.ChannelDto;
import com.han.community.dto.PostDto;
import com.han.community.dto.UserDto;
import com.han.community.entity.Channel;
import com.han.community.entity.Post;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    private static final int HOT_THRESHOLD = 20;

    @Transactional
    public PostDto.Response create(PostDto.CreateRequest requestDto, Long userId) {

        Channel channel = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));
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
    public PostDto.DetailResponse getDetail(Long id) {

        Post post = postRepository.findByIdWithDetails(id).orElseThrow();

        return PostDto.DetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userInfo(UserDto.Response.from(post.getUser()))
                .channelInfo(ChannelDto.Response.from(post.getChannel()))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .commentCount(post.getCommentCount())
                .reactionStatus(null)
                .bookmarked(false)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Transactional
    public PostDto.Response update(Long postId, PostDto.UpdateRequest requestDto, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(!userId.equals(post.getUser().getId()))
            throw new BusinessException(ErrorCode.FORBIDDEN);

        post.update(requestDto);

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
        return page.map(post -> PostDto.Response.from(post));
    }

    @Transactional
    public Page<PostDto.Response> getHotPostPage(String period, Pageable pageable) {

        LocalDateTime from = switch(period) {
            case "24h" -> LocalDateTime.now().minusDays(1);
            case "7d" -> LocalDateTime.now().minusDays(7);
            case "30d" -> LocalDateTime.now().minusDays(30);
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT);
        };

        Page<Post> page = postRepository.findHotPosts(HOT_THRESHOLD, from, pageable);

        return page.map(post -> PostDto.Response.from(post));
    }
}
