package com.example.web.service;

import com.example.web.model.BookDTO;
import com.example.db.model.Book;
import com.example.db.repository.book.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.web.utils.BookUtils.getBookDTOS;

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

}
