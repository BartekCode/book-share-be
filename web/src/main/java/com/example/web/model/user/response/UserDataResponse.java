package com.example.web.model.user.response;

import java.util.List;

public record UserDataResponse(
        String userId,
        String username,
        String email,
        List<Long> userBooks,
        List<Long> borrowedBooks,
        List<Long> readBooks,
        List<Long> likedBooks
) {
}
