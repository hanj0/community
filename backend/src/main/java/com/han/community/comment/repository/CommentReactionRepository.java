package com.han.community.comment.repository;

import com.han.community.comment.entity.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByCommentIdAndUserId(Long commentId, Long userId);

    List<CommentReaction> findByUserIdAndCommentIdIn(Long userId, List<Long> commentIds);
}
