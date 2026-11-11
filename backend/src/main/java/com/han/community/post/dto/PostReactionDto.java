package com.han.community.post.dto;

import com.han.community.post.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public class PostReactionDto {

    public record Request(
            @NotNull ReactionType type
    ) {}
}
