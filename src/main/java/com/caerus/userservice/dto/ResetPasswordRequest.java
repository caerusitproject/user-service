package com.caerus.userservice.dto;

import com.caerus.userservice.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "Password is required")
        @StrongPassword
        String newPassword
) {}
