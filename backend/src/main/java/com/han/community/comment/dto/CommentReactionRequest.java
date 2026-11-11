package com.han.community.comment.dto;

import com.han.community.post.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public record CommentReactionRequest(
        @NotNull ReactionType type
) {
}
