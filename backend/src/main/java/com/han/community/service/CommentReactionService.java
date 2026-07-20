package com.han.community.service;

import com.han.community.dto.CommentReactionDto;
import com.han.community.entity.*;
import com.han.community.event.ReactionEvent;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.AdvisoryLockRepository;
import com.han.community.repository.CommentReactionRepository;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
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
                new ReactionEvent(
                        userId,
                        TargetType.COMMENT,
                        commentId,
                        comment.getPost().getId(),
                        comment.getUser().getId()
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
