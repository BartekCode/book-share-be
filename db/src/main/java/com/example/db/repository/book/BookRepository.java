package com.example.db.repository.book;

import com.example.db.model.Book;
import com.example.db.model.UserBookData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class BookRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public BookRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    public List<Book> getAllBooks() {
        return jdbcClient.sql("""
                        WITH comments AS (
                            SELECT book_id, array_agg(c.content) AS comments
                            FROM book_share.comment c
                            GROUP BY book_id
                        ),
                        likes AS (
                            SELECT book_id, COUNT(*) AS likes_number
                            FROM book_share.book_like
                            GROUP BY book_id
                        ),
                        isBorrowed AS (
                            SELECT book_id,
                                bool_or(status = 'Accepted' OR status = 'Pending') AS isBorrowed
                            FROM book_share.book_rent_request
                            GROUP BY book_id
                        )
                        SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                               b.created_at AS dateAdded, b.genre, 
                               COALESCE(c.comments, '{}') AS comments, 
                               COALESCE(l.likes_number, 0) AS likesNumber, 
                               COALESCE(ib.isBorrowed, false) AS isBorrowed
                        FROM book_share.book b
                        LEFT JOIN comments c ON b.id = c.book_id
                        LEFT JOIN likes l ON b.id = l.book_id
                        LEFT JOIN isBorrowed ib ON b.id = ib.book_id
                        ORDER BY b.created_at DESC
                        """)
                .query((rs, rowNum) -> new Book(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("imageUrl"),
                        rs.getString("description"),
                        rs.getObject("dateAdded", LocalDate.class),
                        rs.getString("genre"),
                        Arrays.stream((Object[]) rs.getArray("comments").getArray())
                                .map(String::valueOf)
                                .toList(),
                        rs.getInt("likesNumber"),
                        rs.getBoolean("isBorrowed")
                ))
                .list();
    }

    //    TODO dodac enkodowanie hasla w core serwis
    public String saveUser(String username, String email, String password) {
        return jdbcClient.sql("""
                        INSERT INTO book_share.user (username, email, password) VALUES (:name, :email, :password)
                        RETURNING id;
                        """)
                .param("username", username)
                .param("email", email)
                .param("password", password)
                .query(String.class)
                .single();
    }

    public UserBookData getUserDataByNameAndPassword(String username, String password) {
        return jdbcClient.sql("""
                        WITH logged_user AS (
                            SELECT id, username, email
                            FROM book_share.user
                            WHERE username = :username AND password = :password
                        ),
                        user_books AS (
                            SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                                   b.created_at AS dateAdded, b.genre, b.user_id
                            FROM book_share.book b
                            JOIN logged_user u ON b.user_id = u.id
                        ),
                        borrowed_books AS (
                            SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                                   b.created_at AS dateAdded, b.genre, br.user_id
                            FROM book_share.book b
                            JOIN book_share.book_rent_request br ON br.book_id = b.id
                            JOIN logged_user u ON br.user_id = u.id
                            WHERE br.status = 'Accepted' OR br.status = 'Pending'
                        ),
                        liked_books AS (
                            SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                                   b.created_at AS dateAdded, b.genre, bl.user_id
                            FROM book_share.book b
                            JOIN book_share.book_like bl ON bl.book_id = b.id
                            JOIN logged_user u ON bl.user_id = u.id
                        )
                        SELECT 
                            u.id,
                            COALESCE(json_agg(DISTINCT ub) FILTER (WHERE ub.id IS NOT NULL), '[]') AS user_books,
                            COALESCE(json_agg(DISTINCT bb) FILTER (WHERE bb.id IS NOT NULL), '[]') AS borrowed_books,
                            COALESCE(json_agg(DISTINCT lb) FILTER (WHERE lb.id IS NOT NULL), '[]') AS liked_books
                        FROM logged_user u
                        LEFT JOIN user_books ub ON ub.user_id = u.id
                        LEFT JOIN borrowed_books bb ON bb.user_id = u.id
                        LEFT JOIN liked_books lb ON lb.user_id = u.id
                        GROUP BY u.id
                        """)
                .param("username", username)
                .param("password", password)
                .query((rs, rowNum) -> {
                    try {
                        String userId = rs.getString("id");
                        List<Book> userBooks = objectMapper.readValue(
                                rs.getString("user_books"),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
                        );
                        List<Book> borrowedBooks = objectMapper.readValue(
                                rs.getString("borrowed_books"),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
                        );
                        List<Book> likedBooks = objectMapper.readValue(
                                rs.getString("liked_books"),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
                        );
                        userBooks.forEach(c -> System.out.println(c.imageUrl()));
                        return new UserBookData(userId, userBooks, borrowedBooks, likedBooks);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .single();
    }

    public String getUserId(String userId) {
        return jdbcClient.sql("""
                        SELECT id FROM book_share.user 
                        WHERE username = :username AND password = :password
                                      """)
                .param(userId, userId)
                .query(String.class)
                .single();
    }
}
