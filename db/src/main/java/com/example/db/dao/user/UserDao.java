package com.example.db.dao.user;

import com.example.db.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class UserDao {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public UserDao(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
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

    public User getUserDataByName(String username) {
        return jdbcClient.sql("""
                        SELECT 
                            u.id,
                            u.username,
                            u.email,
                            u.password
                        FROM book_share.user u
                        WHERE u.username = :username
                        """)
                .param("username", username)
                .query(User.class)
                .single();
    }

}
