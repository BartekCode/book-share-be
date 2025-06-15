package com.example.db.model.book;

public record BookBorrowRequest(
        String bookId,
        String userId,
        String status,
        String message,
        String imageUrl,
        String username
) {
}
