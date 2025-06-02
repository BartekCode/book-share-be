package com.example.book.model;

import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Validated
public record BookDTO(
    String id,
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
