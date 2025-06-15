package com.example.db.model.user;

public record User(
        String id,
        String username,
        String email,
        String password
) { }
