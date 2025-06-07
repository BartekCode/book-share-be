package com.example.db.repository.book;

import com.example.db.model.Book;
import com.example.db.model.NewBook;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class BookRepository {

    private final JdbcClient jdbcClient;

    public BookRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
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
                        rs.getLong("id"),
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

    public void insertBook(NewBook book) {
        jdbcClient.sql("""
                        INSERT INTO book_share.book (user_id, title, author, image_url, description, genre)
                        VALUES (:userId::uuid, :title, :author, :imageUrl, :description, :genre)
                        """)
                .param("title", book.title())
                .param("author", book.author())
                .param("imageUrl", book.imageUrl())
                .param("description", book.description())
                .param("genre", book.genre())
                .param("userId", book.userId())
                .update();
    }

    //TODO    moze getUserData a reszte filtrowac na frontendzie
    public List<Book> getUserBooks(String userId) {
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
                            SELECT br.book_id,
                                bool_or(status = 'Accepted' OR status = 'Pending') AS isBorrowed
                            FROM book_share.book_rent_request br
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
                        WHERE b.user_id = :userId::uuid
                        ORDER BY b.created_at DESC
                        """)
                .param("userId", userId)
                .query((rs, rowNum) -> new Book(
                        rs.getLong("id"),
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

//Potrzeba tylko idki polubionych ksiazek, moze ksiazek usera tez
    public List<Long> getUserLikedBooks(String userId) {
        return jdbcClient.sql("""
                            SELECT bl.book_id
                            FROM book_share.book_like bl
                            WHERE bl.user_id = :userId::uuid
                            ORDER BY bl.book_id
                        """)
                .param("userId", userId)
                .query(Long.class)
                .list();

    }

    public List<Long> getUserBorrowedBooks(String userId) {
        return jdbcClient.sql("""
                            SELECT br.book_id
                            FROM book_share.book_rent_request br
                            WHERE br.user_id = :userId::uuid
                            AND (br.status = 'Accepted' OR br.status = 'Pending')
                            ORDER BY br.book_id
                        """)
                .param("userId", userId)
                .query(Long.class)
                .list();
    }

    public List<Long> getUserReadBooks(String userId) {
        return jdbcClient.sql("""
                            SELECT br.book_id
                            FROM book_share.book_rent_request br
                            WHERE br.user_id = :userId::uuid
                            AND (br.status = 'Returned')
                            ORDER BY br.book_id
                        """)
                .param("userId", userId)
                .query(Long.class)
                .list();
    }
}
