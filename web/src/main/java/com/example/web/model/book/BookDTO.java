package com.example.web.model.book;

import com.example.web.model.Genre;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Validated
public record BookDTO(
    Long id,
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
