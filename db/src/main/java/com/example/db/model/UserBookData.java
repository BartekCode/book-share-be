package com.example.db.model;

import java.util.List;

public record UserBookData(
        String id,
        List<Book> userBooks,
        List<Book> borrowedBooks,
        List<Book> likedBooks
) { }
