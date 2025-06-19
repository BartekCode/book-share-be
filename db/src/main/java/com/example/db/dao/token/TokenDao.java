package com.example.db.dao.token;

import com.example.db.model.token.Token;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                .param("expiresAt", token.expiresAt())
                .update();
    }

    public Optional<TokenData> retrieveToken(String token, String username) {
        return jdbcClient.sql("""
                        SELECT t.user_id, u.username, u.email, t.token, t.expires_at, u.enabled FROM book_share.token t
                        JOIN book_share.user u  ON u.id = t.user_id   
                        WHERE token = :token
                        AND username = :username
                        """)
                .param("token", token)
                .param("username", username)
                .query(TokenData.class)
                .optional();
    }
}
