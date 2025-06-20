package com.example.db.dao.token;

import java.time.LocalDateTime;

public record TokenData(
        String userId,
        String username,
        String email,
        String token,
        LocalDateTime expiresAt,
        boolean enabled
) {
}
