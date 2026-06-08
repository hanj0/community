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
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private static final int HOT_THRESHOLD = 20;
    private static final int PAGE_SIZE_LIMIT = 100;

    @Transactional
    public PostDto.SummaryResponse create(PostDto.CreateRequest requestDto, Long userId) {

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

        return PostDto.SummaryResponse.from(savePost);
    }

    @Transactional
    public PostDto.DetailResponse getDetail(Long id, boolean alreadyViewed) {

        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(!alreadyViewed) {
            postRepository.increaseViewCount(id);
        }

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
    public PostDto.SummaryResponse update(Long postId, PostDto.UpdateRequest requestDto, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(!post.getUser().getId().equals(userId))
            throw new BusinessException(ErrorCode.FORBIDDEN);

        post.update(requestDto);

        return PostDto.SummaryResponse.from(post);
    }

    @Transactional
    public void delete(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // soft delete로 수정 필요
        commentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    @Transactional
    public Page<PostDto.SummaryResponse> getPostPage(Long channelId, String search, String sort, Pageable pageable) {

        Sort pageSort = switch(sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "likes" -> Sort.by(Sort.Direction.DESC, "likeCount");
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "comments" -> Sort.by(Sort.Direction.DESC, "commentCount");
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST);
        };

        Pageable finalPageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), PAGE_SIZE_LIMIT),
                pageSort
        );

        Page<Post> page;

        if(channelId != null && StringUtils.hasText(search)) page = postRepository.findAllByChannelIdAndSearch(channelId, search, finalPageable);
        else if(channelId != null) page = postRepository.findAllByChannelId(channelId, finalPageable);
        else if(StringUtils.hasText(search)) page = postRepository.findAllBySearch(search, finalPageable);
        else page = postRepository.findAllWithChannelAndUser(finalPageable);

        return page.map(post -> PostDto.SummaryResponse.from(post));
    }

    @Transactional
    public Page<PostDto.SummaryResponse> getHotPostPage(String period, Pageable pageable) {

        LocalDateTime from = switch(period) {
            case "24h" -> LocalDateTime.now().minusDays(1);
            case "7d" -> LocalDateTime.now().minusDays(7);
            case "30d" -> LocalDateTime.now().minusDays(30);
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST);
        };

        Page<Post> page = postRepository.findHotPosts(HOT_THRESHOLD, from, pageable);

        return page.map(post -> PostDto.SummaryResponse.from(post));
    }
}
