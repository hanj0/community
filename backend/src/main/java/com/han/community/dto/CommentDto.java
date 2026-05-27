package com.han.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    public static class CreateRequest {

        private Long parentCommentId;
        private String content;
    }

    @Getter
    public static class UpdateRequest {

        private Long id;
        private String content;
    }

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private String content;
        private Long parentId;
        private UserDto.Response userInfo;
        private int likeCount;
        private int dislikeCount;
        private int replyCount;
        private boolean reactionStatus;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
