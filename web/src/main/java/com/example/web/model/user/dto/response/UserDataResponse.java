package com.example.web.model.user.dto.response;

import com.example.web.model.book.dto.response.BookResponse;

import java.util.List;

public record UserDataResponse(
        String userId,
        String username,
        String email,
        List<BookResponse> userBooks,
        List<BookResponse> borrowedBooks,
        List<BookResponse> readBooks,
        List<BookResponse> likedBooks
) {
}
