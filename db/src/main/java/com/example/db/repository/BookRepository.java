package com.example.db.repository;

import com.example.db.model.Book;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class BookRepository {

    private final JdbcClient jdbcClient;

    public BookRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Book> getAllBooks() {
      return  jdbcClient.sql("""
                WITH comments AS (
                    SELECT book_id, array_agg(comment) AS comments
                    FROM book_share.comment
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
                       ib.isBorrowed
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
}
