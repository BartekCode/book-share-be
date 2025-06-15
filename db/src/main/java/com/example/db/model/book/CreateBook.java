package com.example.db.model.book;

public record CreateBook(
        String userId,
        String title,
        String author,
        String imageUrl,
        String description,
        String genre
) {
}
