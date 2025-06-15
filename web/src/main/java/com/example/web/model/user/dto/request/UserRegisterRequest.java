package com.example.web.model.user.dto.request;

public record UserRegisterRequest(
       String username,
       String password,
       String email
) {
}
