package com.han.community.dto;

import com.han.community.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class AuthDto {

    @Getter
    public static class LoginRequest {

        @NotBlank
        private String email;
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

    public record Response(
            Long id,
            String username,
            String email
    ) {

        public static Response from(User user) {
            return new Response(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
        }
    }

    public record StatusResponse(
            Long id,
            String username,
            String role
    ) {

        public static StatusResponse from(User user) {
            return new StatusResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().getAuthority()
            );
        }
    }

    public record ChangePasswordRequest(
            String currentPassword,
            String newPassword
    ) {}
}
