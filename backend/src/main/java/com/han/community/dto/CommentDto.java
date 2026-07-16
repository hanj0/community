package com.han.community.dto;

import com.han.community.entity.ReactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    public static class CreateRequest {

        private Long parentId;
        private String content;
    }

    @Getter
    public static class UpdateRequest {

        private String content;
    }

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private String content;
        private Long parentId;
        private UserDto.AuthorResponse authorInfo;
        private int likeCount;
        private int dislikeCount;
        private int replyCount;
        private ReactionType reactionType;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
