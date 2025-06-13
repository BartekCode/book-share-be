package com.example.web.service;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.Book;
import com.example.db.model.NewBook;
import com.example.db.repository.book.BookRepository;
import com.example.db.repository.book.BookRequestData;
import com.example.web.model.book.BookDTO;
import com.example.web.model.book.BookRequestDTO;
import com.example.web.model.book.BookRequestStatus;
import com.example.web.model.book.NewBookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.example.web.utils.BookUtils.getBookDTOS;

@Service
public class BookService {

    private BookRepository bookRepository;
    private TransactionTemplate tx;

    @Autowired
    public BookService(BookRepository bookRepository, TransactionTemplate tx) {
        this.bookRepository = bookRepository;
        this.tx = tx;
    }

    public BookService() {
    }

    @LogExecutionTime
    public List<BookDTO> getBooks(int page, int size) {
        List<Book> allBooks = bookRepository.getAllBooks(page, size);
        return getBookDTOS(allBooks);
    }

    @LogExecutionTime
    public void addBook(NewBookDTO book) {
        tx.execute(status -> {
            bookRepository.insertBook(new NewBook(
                    book.userId(),
                    book.title(),
                    book.author(),
                    book.imageUrl(),
                    book.description(),
                    book.genre().getCode()
            ));
            return null;
        });
    }

    @LogExecutionTime
    public void removeBook(String userId, Long bookId) {
        tx.execute(status -> {
            bookRepository.removeBook(userId, bookId);
            return null;
        });
    }

    @LogExecutionTime
    public Long borrowBook(String userId, Long bookId) {
        return tx.execute(status -> bookRepository.borrowBook(userId, bookId));
    }

    @LogExecutionTime
    public Long returnBook(String userId, Long bookId) {
        return tx.execute(status -> bookRepository.returnBook(userId, bookId));
    }

    @LogExecutionTime
    public Long acceptBookRequest(String requestUserId, String ownerUserId, Long bookId, Boolean isAccepted) {
        BookRequestStatus status = isAccepted ? BookRequestStatus.ACCEPTED : BookRequestStatus.REJECTED;
        return tx.execute(status1 -> bookRepository.updateBookRequest(requestUserId, ownerUserId, bookId, BookRequestStatus.PENDING.getStatus(), status.getStatus()));
    }

    @LogExecutionTime
    public List<BookRequestDTO> checkRequests(String userId) {
        return mapToBookRequestDTOs(bookRepository.checkPendingRequests(userId));
    }

    private List<BookRequestDTO> mapToBookRequestDTOs(List<BookRequestData> dataList) {
        return dataList.stream()
                .map(data -> new BookRequestDTO(
                        data.bookId(),
                        data.userId(),
                        data.message(),
                        data.imageUrl(),
                        data.username()
                ))
                .toList();
    }

}