package com.han.community.dto;

import com.han.community.entity.Post;
import com.han.community.entity.ReactionType;
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
    public static class SummaryResponse {

        private Long id;
        private String title;
        private String content;
        private UserDto.AuthorResponse authorInfo;
        private int viewCount;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static SummaryResponse from(Post post) {
            return new SummaryResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    UserDto.AuthorResponse.from(post.getUser()),
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

        private UserDto.AuthorResponse authorInfo;
        private ChannelDto.Response channelInfo;

        private int viewCount;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;

        private ReactionType reactionType;
        private boolean bookmarked;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private boolean canEdit;
        private boolean canDelete;
    }

    public static class TitleResponse {
        private Long id;
        private String title;
    }
}
