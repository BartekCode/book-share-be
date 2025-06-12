package com.example.db.model;

public record UserData(
        String id,
        String username,
        String email,
        String password
) { }
