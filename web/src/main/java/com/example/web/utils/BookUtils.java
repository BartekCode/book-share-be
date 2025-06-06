package com.example.web.utils;

import com.example.web.model.BookDTO;
import com.example.web.model.Genre;
import com.example.db.model.Book;

import java.util.List;

public record BookUtils() {

    public static List<BookDTO> getBookDTOS(List<Book> allBooks) {
        return allBooks.stream()
                .map(book -> new BookDTO(
                        book.id(),
                        book.title(),
                        book.author(),
                        book.imageUrl(),
                        book.description(),
                        book.dateAdded(),
                        Genre.fromCode(book.genre()),
                        book.comments(),
                        book.likesNumber(),
                        book.isBorrowed()))
                .toList();
    }
}
