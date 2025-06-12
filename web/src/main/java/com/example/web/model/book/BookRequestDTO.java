package com.example.web.model.book;

public record BookRequestDTO(
        String bookId,
        String userId,
        String message
) {
}
