package com.example.web.model.book.dto.response;

public record BookBorrowResponse(
        String bookId,
        String requestId,
        String message,
        String imageUrl,
        String username,
        String title
) {
}
