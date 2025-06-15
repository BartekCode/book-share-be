package com.example.web.utils.book;

import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.model.common.enums.Genre;
import com.example.db.model.book.Book;
import com.example.web.model.book.dto.response.UserBookResponse;

import java.util.List;

public record BookUtils() {

    public static List<BookResponse> getBookDTOS(List<Book> allBooks) {
        return allBooks.stream()
                .map(book -> new BookResponse(
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

    public static List<UserBookResponse> getUserBooksDTO(String userId, List<Book> allBooks) {
        return allBooks.stream()
                .map(book -> new UserBookResponse(
                        book.id(),
                        userId,
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
