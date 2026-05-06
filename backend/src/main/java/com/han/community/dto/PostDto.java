package com.han.community.dto;

import com.han.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    public static class CreateRequest {

        private String title;
        private String content;

        public String getTitle() {
            return this.title;
        }
        public String getContent() {
            return this.content;
        }
    }

    @Getter
    public static class UpdateRequest {

        private Long id;
        private String title;
        private String content;
    }

    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String content;

        public Response() {}

        public Response(Long id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }

        public static Response from(Post post) {
            return new Response(
                post.getId(),
                post.getTitle(),
                post.getContent()
            );
        }
    }

    @AllArgsConstructor
    public static class DetailResponse {
        private Long id;
        private String title;
        private String content;

        private int viewCount;
        private int likeCount;
        private int dislikeCount;
        private int CommentCount;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static DetailResponse from(Post post) {
            return new DetailResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getDislikeCount(),
                    post.getCommentCount(),
                    post.getCreatedAt(),
                    post.getUpdatedAt()
            );
        }
    }
}
