package com.han.community.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(

    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String email
) {}
