package com.example.web.model.user.request;

public record UserRegisterRequest(
       String username,
       String password,
       String email
) {
}
