package com.example.web.controllers;

import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.model.book.dto.response.BookBorrowResponse;
import com.example.web.model.book.dto.request.BookCreateRequest;
import com.example.web.service.book.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public List<BookResponse> getAllBooks(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        return bookService.getBooks(page, size);
    }

    @PostMapping("/add")
    public void addBook(
            @RequestBody() BookCreateRequest book
    ) {
        bookService.addBook(book);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<Void> removeBook(
            @PathVariable("bookId") Long bookId,
            @RequestParam("userId") String userId
    ) {
        bookService.removeBook(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/borrow/{bookId}")
    public Long borrowBook(
            @PathVariable("bookId") Long bookId,
            @RequestParam("userId") String userId
    ) {
        return bookService.borrowBook(userId, bookId);
    }

    @PutMapping("/return/{bookId}")
    public Long returnBook(
            @PathVariable("bookId") Long bookId,
            @RequestParam("userId") String userId
    ) {
        return bookService.returnBook(userId, bookId);
    }

    @PutMapping("/accept/{bookId}")
    public Long acceptBookRequest(
            @PathVariable("bookId") Long bookId,
            @RequestParam("requestUserId") String requestUserId,
            @RequestParam("ownerUserId") String ownerUserId,
            @RequestParam("isAccepted") Boolean isAccepted
    ) {
        return bookService.acceptBorrowRequest(requestUserId, ownerUserId, bookId, isAccepted);
    }

    @GetMapping("/request/{userId}")
    public List<BookBorrowResponse> getPendingRequests(
            @PathVariable("userId") String userId
    ) {
        return bookService.checkBorrowRequests(userId);
    }
}
