package com.example.db.repository.user;

import com.example.db.model.Book;
import com.example.db.model.UserBookData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public UserRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
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

    public String getUserPasswordByUsername(String username) {
        return jdbcClient.sql("""
                        SELECT password
                        FROM book_share.user
                        WHERE username = :username
                        """)
                .param("username", username)
                .query(String.class)
                .single();
    }

    public UserBookData getUserDataByName(String username) {
        return jdbcClient.sql("""
                        WITH logged_user AS (
                            SELECT id, username, email
                            FROM book_share.user
                            WHERE username = :username
                        ),
                        user_books AS (
                            SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                                   b.created_at AS dateAdded, b.genre, b.user_id
                            FROM book_share.book b
                            JOIN logged_user u ON b.user_id = u.id
                        )
                        SELECT 
                            u.id,
                            u.email,
                            u.username, 
                            COALESCE(json_agg(DISTINCT ub) FILTER (WHERE ub.id IS NOT NULL), '[]') AS user_books
                        FROM logged_user u
                        LEFT JOIN user_books ub ON ub.user_id = u.id
                        GROUP BY u.id, u.email, u.username
                        """)
                .param("username", username)
                .query((rs, rowNum) -> {
                    try {
                        String userId = rs.getString("id");
                        String email = rs.getString("email");
                        List<Book> userBooks = objectMapper.readValue(
                                rs.getString("user_books"),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
                        );
//                        List<Book> borrowedBooks = objectMapper.readValue(
//                                rs.getString("borrowed_books"),
//                                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
//                        );
//                        List<Book> likedBooks = objectMapper.readValue(
//                                rs.getString("liked_books"),
//                                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
//                        );
                        return new UserBookData(userId, username, email, userBooks);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .single();
    }

}
