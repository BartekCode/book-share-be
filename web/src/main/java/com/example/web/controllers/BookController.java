package com.example.web.controllers;

import com.example.core.model.user.User;
import com.example.web.model.book.dto.request.BookCreateRequest;
import com.example.web.model.book.dto.response.BookBorrowResponse;
import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.service.book.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
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
            @RequestBody() BookCreateRequest book,
            @AuthenticationPrincipal User userData
    ) {
        bookService.addBook(book, userData.id());
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<Void> removeBook(
            @PathVariable("bookId") Long bookId,
            @AuthenticationPrincipal User userData
    ) {
        bookService.removeBook(userData.id(), bookId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/borrow/{bookId}")
    public Long borrowBook(
            @PathVariable("bookId") Long bookId,
            @AuthenticationPrincipal User userData
    ) {
        return bookService.borrowBook(userData.id(), bookId);
    }

    @PutMapping("/return/{bookId}")
    public Long returnBook(
            @PathVariable("bookId") Long bookId,
            @AuthenticationPrincipal User userData
    ) {
        return bookService.returnBook(userData.id(), bookId);
    }

    @PutMapping("/accept/{bookId}")
    public Long acceptBookRequest(
            @PathVariable("bookId") Long bookId,
            @AuthenticationPrincipal User userData,
            @RequestParam("isAccepted") Boolean isAccepted,
            @RequestParam("requestId") Long requestId
    ) {
        return bookService.acceptBorrowRequest(requestId, bookId, isAccepted);
    }

    @GetMapping("/request")
    public List<BookBorrowResponse> getPendingRequests(
            @AuthenticationPrincipal User userData
    ) {
        return bookService.checkBorrowRequests(userData.id());
    }
}
