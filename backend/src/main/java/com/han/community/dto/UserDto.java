package com.han.community.dto;

import com.han.community.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


public class UserDto {

    public record StatsResponse(
            Long postCount,
            Long commentCount,
            LocalDateTime createdAt
    ) {}

    public record MyCommentResponse(
            Long id,
            String content,
            LocalDateTime createdAt,
            Long postId,
            String postTitle
    ) {}

    @Getter
    @Builder
    public static class authorResponse {

        private Long id;
        private String username;

        public static authorResponse from(User user) {
            return authorResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .build();
        }
    }
}
