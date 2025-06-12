package com.example.web.model.user.response;

import com.example.web.model.book.BookDTO;

import java.util.List;

public record UserDataResponse(
        String userId,
        String username,
        String email,
        List<BookDTO> userBooks,
        List<BookDTO> borrowedBooks,
        List<BookDTO> readBooks,
        List<BookDTO> likedBooks
) {
}
