package com.example.db.model;

import java.time.LocalDate;
import java.util.List;

public record Book(
    String id,
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
