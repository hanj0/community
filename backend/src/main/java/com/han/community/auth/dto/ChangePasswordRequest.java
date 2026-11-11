package com.han.community.auth.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
