package com.example.web.model.book.dto.response;

import com.example.web.model.common.enums.Genre;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Validated
public record UserBookResponse(
    Long id,
    String userId,
    String title,
    String author,
    String imageUrl,
    String description,
    LocalDate dateAdded,
    Genre genre,
    List<String> comments,
    Integer likesNumber,
    boolean isBorrowed
) {
}
