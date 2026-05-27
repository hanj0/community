package com.han.community.service;

import com.han.community.dto.CommentDto;
import com.han.community.dto.UserDto;
import com.han.community.entity.Comment;
import com.han.community.entity.Post;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Page<CommentDto.Response> getComments(Long postId, Pageable pageable) {

        return commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable).map(
                c -> CommentDto.Response.builder()
                        .userInfo(UserDto.Response.from(c.getUser()))
                        .id(c.getId())
                        .content(c.getContent())
                        .replyCount(c.getReplyCount())
                        .reactionStatus(false)
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build()
        );
    }

    @Transactional
    public Page<CommentDto.Response> getReplies(Long commentId, Pageable pageable) {

        return commentRepository.findByParentCommentId(commentId, pageable).map(
                c -> CommentDto.Response.builder()
                        .userInfo(UserDto.Response.from(c.getUser()))
                        .id(c.getId())
                        .content(c.getContent())
                        .parentId(c.getParentComment().getId())
                        .likeCount(c.getLikeCount())
                        .dislikeCount(c.getDislikeCount())
                        .replyCount(c.getReplyCount())
                        .reactionStatus(false)
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

        Comment parentComment = getParentComment(postId, requestDto.getParentCommentId());

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .content(requestDto.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentDto.Response.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .parentId(savedComment.getParentComment() != null ? savedComment.getParentComment().getId() : null)
                .userInfo(UserDto.Response.from(user))
                .reactionStatus(false)
                .replyCount(0)
                .likeCount(0)
                .dislikeCount(0)
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
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment getParentComment(Long postId, Long parentCommentId) {

        if(parentCommentId == null) return null;

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // 같은 게시글 검증

        return parentComment;
    }
}
