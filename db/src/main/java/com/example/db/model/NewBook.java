package com.example.db.model;

public record NewBook(
        String userId,
        String title,
        String author,
        String imageUrl,
        String description,
        String genre
) {
}
