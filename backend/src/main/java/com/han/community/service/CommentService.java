package com.han.community.service;

import com.han.community.dto.CommentDto;
import com.han.community.dto.UserDto;
import com.han.community.entity.*;
import com.han.community.event.NotificationEvent;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.CommentReactionRepository;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Page<CommentDto.Response> getComments(Long postId, @Nullable Long userId, Pageable pageable) {

        Page<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable);

        List<Long> commentIds = comments.getContent().stream()
                .map(comment -> comment.getId())
                .toList();

        Map<Long, ReactionType> userReactions = (userId == null || commentIds.isEmpty())
                ? Collections.emptyMap()
                : commentReactionRepository.findByUserIdAndCommentIdIn(userId, commentIds)
                    .stream()
                    .collect(Collectors.toMap(
                            cr -> cr.getComment().getId(),
                            cr -> cr.getType()
                    ));

        return comments.map(
                c -> CommentDto.Response.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .authorInfo(UserDto.AuthorResponse.from(c.getUser()))
                        .likeCount(c.getLikeCount())
                        .dislikeCount(c.getDislikeCount())
                        .replyCount(c.getReplyCount())
                        .reactionType(userReactions.get(c.getId()))
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build()
        );
    }

    @Transactional
    public Page<CommentDto.Response> getReplies(Long commentId, @Nullable Long userId, Pageable pageable) {

        Page<Comment> replies = commentRepository.findByParentCommentId(commentId, pageable);

        List<Long> commentIds = replies.getContent().stream()
                .map(comment -> comment.getId())
                .toList();

        Map<Long, ReactionType> userReactions = (userId == null || commentIds.isEmpty())
                ? Collections.emptyMap()
                : commentReactionRepository.findByUserIdAndCommentIdIn(userId, commentIds)
                    .stream()
                    .collect(Collectors.toMap(
                            cr -> cr.getComment().getId(),
                            cr -> cr.getType()
                    ));

        return replies.map(
                c -> CommentDto.Response.builder()
                        .authorInfo(UserDto.AuthorResponse.from(c.getUser()))
                        .id(c.getId())
                        .content(c.getContent())
                        .parentId(c.getParentComment().getId())
                        .likeCount(c.getLikeCount())
                        .dislikeCount(c.getDislikeCount())
                        .replyCount(c.getReplyCount())
                        .reactionType(userReactions.get(c.getId()))
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build()
        );
    }

    @Transactional
    public CommentDto.Response create(CommentDto.CreateRequest requestDto, Long postId, Long userId) {

        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Long parentId = requestDto.getParentId();
        Comment parentComment = null;
        if(parentId != null) {
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
            commentRepository.incrementReplyCount(parentId);
        }

        postRepository.incrementCommentCount(postId);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .content(requestDto.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // todo: 한번에 가져오는게 성능이 좋으면 post조회할때 join해서 가져오게
        boolean shouldNotify = parentId == null
                ? !userId.equals(post.getUser().getId())
                : !userId.equals(parentComment.getUser().getId());

        if(shouldNotify) {
            publisher.publishEvent(new NotificationEvent(
                    userId,
                    parentId == null ? TargetType.POST : TargetType.COMMENT,
                    parentId == null ? postId : parentComment.getId(),
                    postId,
                    parentId == null ? post.getUser().getId() : parentComment.getUser().getId(),
                    NotificationType.COMMENT,
                    savedComment.getContent()
            ));
        }

        return CommentDto.Response.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .parentId(parentId)
                .authorInfo(UserDto.AuthorResponse.from(user))
                .reactionType(null)
                .replyCount(savedComment.getReplyCount())
                .likeCount(savedComment.getLikeCount())
                .dislikeCount(savedComment.getDislikeCount())
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .build();
    }

    @Transactional
    public CommentDto.Response update(Long commentId, CommentDto.UpdateRequest requestDto, Long userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if(!userId.equals(comment.getUser().getId()))
            throw new BusinessException(ErrorCode.FORBIDDEN);

        comment.update(requestDto.getContent());

        return CommentDto.Response.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .build();
    }

    @Transactional
    public void delete(Long id, Long userId) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if(!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        commentRepository.deleteByParentCommentId(id);
        commentRepository.deleteById(id);

        Long postId = comment.getPost().getId();
        postRepository.syncCommentCount(postId);
    }

    private Comment getParentComment(Long postId, Long parentCommentId) {

        if(parentCommentId == null) return null;

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!postId.equals(parentComment.getPost().getId()))
            throw new BusinessException(ErrorCode.INVALID_REQUEST);

        return parentComment;
    }
}
