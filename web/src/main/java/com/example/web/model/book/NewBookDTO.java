package com.example.web.model.book;

import com.example.web.model.Genre;

public record NewBookDTO(
        String title,
        String author,
        String imageUrl,
        String description,
        Genre genre,
        String userId
) {
}
