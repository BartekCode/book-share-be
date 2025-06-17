package com.example.db.model.token;

import java.time.LocalDateTime;

public record Token(
        String token,
        LocalDateTime expiresAt
) {
}
