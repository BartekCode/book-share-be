package com.example.web.service;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.Book;
import com.example.db.model.NewBook;
import com.example.db.repository.book.BookRepository;
import com.example.web.model.book.BookDTO;
import com.example.web.model.book.NewBookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.web.utils.BookUtils.getBookDTOS;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookService() {
    }

    @LogExecutionTime
    public List<BookDTO> getBooks() {
        List<Book> allBooks = bookRepository.getAllBooks();
        return getBookDTOS(allBooks);
    }

    @LogExecutionTime
    public void addBook(NewBookDTO book) {
        bookRepository.insertBook(new NewBook(
                book.userId(),
                book.title(),
                book.author(),
                book.imageUrl(),
                book.description(),
                book.genre().getCode()
        ));
    }

}
