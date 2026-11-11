package com.han.community.post.service;

import com.han.community.bookmark.repository.BookmarkRepository;
import com.han.community.channel.dto.ChannelResponse;
import com.han.community.channel.entity.Channel;
import com.han.community.channel.repository.ChannelRepository;
import com.han.community.comment.repository.CommentRepository;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.post.dto.PostDto;
import com.han.community.post.entity.Post;
import com.han.community.post.entity.PostReaction;
import com.han.community.post.repository.PostReactionRepository;
import com.han.community.post.repository.PostRepository;
import com.han.community.post.viewcount.ViewCountIncrementer;
import com.han.community.user.dto.UserDto;
import com.han.community.user.entity.User;
import com.han.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostReactionRepository postReactionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ViewCountIncrementer viewCountIncrementer;

    private static final int HOT_THRESHOLD = 20;
    private static final int PAGE_SIZE_LIMIT = 100;

    @Transactional
    public PostDto.SummaryResponse create(PostDto.CreateRequest requestDto, Long userId) {

        Channel channel = channelRepository.findById(requestDto.getChannelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));
        User user = userRepository.getReferenceById(userId);

        Post post = Post.builder()
                .channel(channel)
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        Post savePost = postRepository.save(post);

        return PostDto.SummaryResponse.from(savePost);
    }

    public PostDto.DetailResponse view(Long postId, @Nullable Long userId, boolean alreadyViewed) {

        PostDto.DetailResponse response = getDetail(postId, userId);

        if(!alreadyViewed) {
            viewCountIncrementer.increment(postId);
        }

        return response;
    }

    @Transactional
    private PostDto.DetailResponse getDetail(Long postId, @Nullable Long userId) {

        Post post = postRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        PostReaction postReaction = null;
        boolean isBookmarked = false;
        if(userId != null) {
            // todo: reaction을 post조회할때 left join으로 한번에 조회하는 방법도 생각
            postReaction = postReactionRepository.findByPostIdAndUserId(postId, userId)
                    .orElse(null);
            isBookmarked = bookmarkRepository.existsByPostIdAndUserId(postId, userId);
        }

        return PostDto.DetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorInfo(UserDto.AuthorResponse.from(post.getUser()))
                .channelInfo(ChannelResponse.from(post.getChannel()))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .commentCount(post.getCommentCount())
                .reactionType(postReaction == null ? null : postReaction.getType())
                .bookmarked(isBookmarked)
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

        post.delete();
        commentRepository.deleteAllByPost(postId, post.getDeletedAt());
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
