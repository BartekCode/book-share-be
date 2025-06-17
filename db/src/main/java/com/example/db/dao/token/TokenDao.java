package com.example.db.dao.token;

import com.example.db.model.token.Token;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class TokenDao {

    private final JdbcClient jdbcClient;

    public TokenDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void insertToken(String userId, Token token) {
        jdbcClient.sql("""
                    INSERT INTO book_share.token (user_id, token, expires_at)
                    VALUES (:userId::uuid, :token, :expiresAt);
                """)
                .param("userId", userId)
                .param("token", token.token())
                .param("expiresAt", token.expiresAt());
    }
}
