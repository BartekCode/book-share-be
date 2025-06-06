package com.example.db.repository.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public String registerUser(String username, String password, String email) {
        return jdbcClient.sql("""
                        INSERT INTO book_share.user (username, password, email)
                        VALUES (:username, :password, :email)
                        RETURNING id;
                        """)
                .param("username", username)
                .param("password", password)
                .param("email", email)
                .query(String.class)
                .single();
    }

}
