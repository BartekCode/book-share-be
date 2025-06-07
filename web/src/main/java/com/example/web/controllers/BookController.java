package com.example.web.controllers;

import com.example.web.model.book.BookDTO;
import com.example.web.model.book.NewBookDTO;
import com.example.web.service.BookService;
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
    public List<BookDTO> getAllBooks() {
        return bookService.getBooks();
    }

    @PostMapping("/add")
    public void addBook(
            @RequestBody() NewBookDTO book
    ) {
        bookService.addBook(book);
    }
}
