package com.example.web.service;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.Book;
import com.example.db.model.NewBook;
import com.example.db.repository.book.BookRepository;
import com.example.web.model.book.BookDTO;
import com.example.web.model.book.BookRequestStatus;
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
    public List<BookDTO> getBooks(int page, int size) {
        List<Book> allBooks = bookRepository.getAllBooks(page,size);
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

    @LogExecutionTime
    public void removeBook(String userId, Long bookId) {
        bookRepository.removeBook(userId, bookId);
    }

    @LogExecutionTime
    public Long borrowBook(String userId, Long bookId) {
      return bookRepository.borrowBook(userId, bookId);
    }

    @LogExecutionTime
    public Long returnBook(String userId, Long bookId) {
       return bookRepository.returnBook(userId, bookId);
    }

    @LogExecutionTime
    public Long updateBookRequest(String userId, Long bookId, Boolean isAccepted) {
        BookRequestStatus status = isAccepted ? BookRequestStatus.ACCEPTED : BookRequestStatus.REJECTED;
       return bookRepository.updateBookRequest(userId, bookId, status.getStatus());
    }

}