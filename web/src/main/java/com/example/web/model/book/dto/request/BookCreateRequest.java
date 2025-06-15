package com.example.web.model.book.dto.request;

import com.example.web.model.common.enums.Genre;

public record BookCreateRequest(
        String title,
        String author,
        String imageUrl,
        String description,
        Genre genre,
        String userId
) {
}
