package com.han.community.comment.service;

import com.han.community.comment.dto.CommentReactionDto;
import com.han.community.comment.entity.Comment;
import com.han.community.comment.entity.CommentReaction;
import com.han.community.notification.entity.NotificationTargetType;
import com.han.community.notification.entity.NotificationType;
import com.han.community.notification.event.NotificationEvent;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.post.entity.ReactionType;
import com.han.community.global.lock.AdvisoryLockRepository;
import com.han.community.comment.repository.CommentReactionRepository;
import com.han.community.comment.repository.CommentRepository;
import com.han.community.user.repository.UserRepository;
import com.han.community.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentReactionService {

    private final CommentReactionRepository commentReactionRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdvisoryLockRepository advisoryLockRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void reactToComment(Long commentId, Long userId, CommentReactionDto.Request requestDto) {

        advisoryLockRepository.lock(commentId, userId);

        User user = userRepository.getReferenceById(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        CommentReaction reaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId)
                .orElse(null);

        ReactionType oldType = reaction == null ? null : reaction.getType();
        ReactionType newType = requestDto.type();

        if(oldType == newType) return;

        if(reaction == null) {
            commentReactionRepository.save(new CommentReaction(comment, user, newType));
        } else {
            reaction.changeType(newType);
        }

        if(oldType == ReactionType.LIKE) commentRepository.decrementLikeCount(commentId);
        else if(oldType == ReactionType.DISLIKE) commentRepository.decrementDislikeCount(commentId);
        if(newType == ReactionType.LIKE) commentRepository.incrementLikeCount(commentId);
        else if(newType == ReactionType.DISLIKE) commentRepository.incrementDislikeCount(commentId);

        if(newType != ReactionType.LIKE) return;

        publisher.publishEvent(
                new NotificationEvent(
                        userId,
                        NotificationTargetType.COMMENT,
                        commentId,
                        comment.getPost().getId(),
                        comment.getUser().getId(),
                        NotificationType.REACTION,
                        comment.getContent()
                )
        );
    }

    @Transactional
    public void deleteReaction(Long commentId, Long userId) {

        CommentReaction reaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId)
                .orElse(null);

        if(reaction == null) return;

        commentReactionRepository.delete(reaction);

        ReactionType type = reaction.getType();
        if(type == ReactionType.LIKE) commentRepository.decrementLikeCount(commentId);
        else if(type == ReactionType.DISLIKE) commentRepository.decrementDislikeCount(commentId);
    }
}
