package com.han.community.comment.dto;

import com.han.community.post.entity.ReactionType;

public record CommentReactionResponse(
        Long commentId,
        ReactionType type,
        long likeCount,
        long dislikeCount
) {
}
