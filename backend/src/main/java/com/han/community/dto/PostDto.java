package com.han.community.dto;

import com.han.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    public static class CreateRequest {

        private Long channelId;
        private String title;
        private String content;
    }

    @Getter
    public static class UpdateRequest {

        private String title;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private String title;
        private String content;

        private int viewCount;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Post post) {
            return new Response(
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

    @Getter
    @Builder
    public static class DetailResponse {

        private Long id;
        private String title;
        private String content;

        private UserDto.Response userInfo;
        private ChannelDto.Response channelInfo;

        private int viewCount;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;

        private String reactionStatus;
        private boolean bookmarked;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static DetailResponse from(Post post) {
            return DetailResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())

                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .dislikeCount(post.getDislikeCount())
                    .commentCount(post.getCommentCount())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

}
