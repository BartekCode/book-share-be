package com.example.db.model.book;

public record BookBorrowRequest(
        String bookId,
        String requestId,
        String status,
        String message,
        String imageUrl,
        String username,
        String title
) {
}
