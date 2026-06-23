package com.han.community.service;

import com.han.community.dto.CommentReactionDto;
import com.han.community.entity.Comment;
import com.han.community.entity.CommentReaction;
import com.han.community.entity.ReactionType;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.CommentReactionRepository;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CommentReactionService {

    private final CommentReactionRepository commentReactionRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentReactionDto.Response reactToComment(Long commentId, Long userId, CommentReactionDto.Request requestDto) {

        User user = userRepository.getReferenceById(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        CommentReaction reaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId)
                .orElse(null);

        ReactionType oldType = reaction == null ? null : reaction.getType();
        ReactionType newType = requestDto.type();
        if(oldType == newType) {
            return new CommentReactionDto.Response(
                    commentId,
                    newType,
                    comment.getLikeCount(),
                    comment.getDislikeCount()
            );
        }

        if(reaction == null) {
            commentReactionRepository.save(new CommentReaction(comment, user, newType));
        } else {
            reaction.changeType(newType);
        }

        int likeCount = comment.getLikeCount();
        int dislikeCount = comment.getDislikeCount();
        if(oldType == ReactionType.LIKE) { commentRepository.decrementLikeCount(commentId); likeCount--; }
        else if(oldType == ReactionType.DISLIKE) { commentRepository.decrementDislikeCount(commentId); dislikeCount--; }

        if(newType == ReactionType.LIKE) { commentRepository.incrementLikeCount(commentId); likeCount++; }
        else if(newType == ReactionType.DISLIKE) { commentRepository.incrementDislikeCount(commentId); dislikeCount++; }

        return new CommentReactionDto.Response(
                commentId,
                newType,
                likeCount,
                dislikeCount
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
