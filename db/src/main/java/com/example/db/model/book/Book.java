package com.example.db.model.book;

import java.time.LocalDate;
import java.util.List;

public record Book(
    Long id,
    String title,
    String author,
    String imageUrl,
    String description,
    LocalDate dateAdded,
    String genre,
    List<String> comments,
    Integer likesNumber,
    boolean isBorrowed
){}
