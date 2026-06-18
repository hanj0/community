package com.han.community.dto;

import com.han.community.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public class PostReactionDto {

    public record Request(
            @NotNull ReactionType type
    ) {}

    public record Response(
            Long postId,
            ReactionType type,
            long likeCount,
            long dislikeCount
    ) {}
}
