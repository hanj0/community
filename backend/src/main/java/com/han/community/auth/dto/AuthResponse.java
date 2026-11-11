package com.han.community.auth.dto;

import com.han.community.user.entity.User;

public record AuthResponse(
        Long id,
        String username,
        String email
) {

    public static AuthResponse from(User user) {
        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
