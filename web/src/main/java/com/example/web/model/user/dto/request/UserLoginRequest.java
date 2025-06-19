package com.example.web.model.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotBlank(message = "Username is mandatory")
        String username,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password should be 8 characters long minimum")
        String password
) {
}
