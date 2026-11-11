package com.han.community.comment.dto;

import com.han.community.post.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public class CommentReactionDto {

    public record Request(
            @NotNull ReactionType type
    ) {}

    public record Response(
            Long commentId,
            ReactionType type,
            long likeCount,
            long dislikeCount
    ) {}
}
