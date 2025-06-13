package com.example.db.repository.book;

public record BookRequestData(
        String bookId,
        String userId,
        String status,
        String message,
        String imageUrl,
        String username
) {
}
