package com.example.web.service.book;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.book.Book;
import com.example.db.model.book.CreateBook;
import com.example.db.dao.book.BookDao;
import com.example.db.model.book.BookBorrowRequest;
import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.model.book.dto.response.BookBorrowResponse;
import com.example.web.model.common.enums.BookStatus;
import com.example.web.model.book.dto.request.BookCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.example.web.utils.book.BookUtils.getBookDTOS;

@Service
public class BookService {

    private BookDao bookDao;
    private TransactionTemplate tx;

    @Autowired
    public BookService(BookDao bookDao, TransactionTemplate tx) {
        this.bookDao = bookDao;
        this.tx = tx;
    }

    public BookService() {
    }

    @LogExecutionTime
    public List<BookResponse> getBooks(int page, int size) {
        List<Book> allBooks = bookDao.getAllBooks(page, size);
        return getBookDTOS(allBooks);
    }

    @LogExecutionTime
    public void addBook(BookCreateRequest book) {
        tx.execute(status -> {
            bookDao.insertBook(new CreateBook(
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
            bookDao.removeBook(userId, bookId);
            return null;
        });
    }

    @LogExecutionTime
    public Long borrowBook(String userId, Long bookId) {
        return tx.execute(status -> bookDao.borrowBook(userId, bookId));
    }

    @LogExecutionTime
    public Long returnBook(String userId, Long bookId) {
        return tx.execute(status -> bookDao.returnBook(userId, bookId));
    }

    @LogExecutionTime
    public Long acceptBorrowRequest(String requestUserId, String ownerUserId, Long bookId, Boolean isAccepted) {
        BookStatus status = isAccepted ? BookStatus.ACCEPTED : BookStatus.REJECTED;
        return tx.execute(status1 -> bookDao.updateBookRequest(requestUserId, ownerUserId, bookId, BookStatus.PENDING.getStatus(), status.getStatus()));
    }

    @LogExecutionTime
    public List<BookBorrowResponse> checkBorrowRequests(String userId) {
        return mapToBookRequestDTOs(bookDao.checkPendingRequests(userId));
    }

    private List<BookBorrowResponse> mapToBookRequestDTOs(List<BookBorrowRequest> dataList) {
        return dataList.stream()
                .map(data -> new BookBorrowResponse(
                        data.bookId(),
                        data.userId(),
                        data.message(),
                        data.imageUrl(),
                        data.username()
                ))
                .toList();
    }

}