package com.han.community.dto;

import lombok.Builder;
import lombok.Getter;

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
    }
}
