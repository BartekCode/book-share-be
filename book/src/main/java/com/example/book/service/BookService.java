package com.example.book.service;

import com.example.book.model.BookDTO;
import com.example.book.model.Genre;
import com.example.db.model.Book;
import com.example.db.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getBooks() {
        List<Book> allBooks = bookRepository.getAllBooks();
        return getBookDTOS(allBooks);
    }

    private static List<BookDTO> getBookDTOS(List<Book> allBooks) {
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
