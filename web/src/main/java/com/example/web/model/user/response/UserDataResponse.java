package com.example.web.model.user.response;

import com.example.web.model.BookDTO;

import java.util.List;

public record UserDataResponse(
        String userId,
        List<BookDTO> userBooks,
        List<BookDTO> borrowedBooks,
        List<BookDTO> likedBooks
) {
}
