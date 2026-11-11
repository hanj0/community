package com.han.community.auth.dto;

import com.han.community.user.entity.User;

public record AuthStatusResponse(
        Long id,
        String username,
        String role
) {

    public static AuthStatusResponse from(User user) {
        return new AuthStatusResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().getAuthority()
        );
    }
}
