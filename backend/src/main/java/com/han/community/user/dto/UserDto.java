package com.han.community.user.dto;

import com.han.community.user.entity.User;
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
    public static class AuthorResponse {

        private Long id;
        private String username;

        public static AuthorResponse from(User user) {
            return AuthorResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .build();
        }
    }
}
