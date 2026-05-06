package com.han.community.dto;

import com.han.community.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AuthDto {

    @Getter
    public static class LoginRequest {

        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Getter
    public static class SignupRequest {

        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String email;
    }

    @Getter
    @Builder
    public static class LoginResponse {

        private String username;
        private String authorities;
        private String sessionId;
    }

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private String username;
        private String email;

        public static Response from(User user) {
            return Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }
    }
}
