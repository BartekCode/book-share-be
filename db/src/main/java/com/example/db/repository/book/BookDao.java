package com.example.db.repository.book;

import com.example.db.model.book.Book;
import com.example.db.model.book.BookBorrowRequest;
import com.example.db.model.book.CreateBook;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class BookDao {

    private final JdbcClient jdbcClient;

    public BookDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Book> getAllBooks(int page, int pageSize) {
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
                                bool_or(status = 'Accepted') AS isBorrowed
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
                        WHERE b.deleted = false    
                        ORDER BY b.created_at DESC
                        OFFSET (:page - 1) * :pageSize
                        LIMIT :pageSize 
                        """)
                .param("page", page)
                .param("pageSize", pageSize)
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

    public void insertBook(CreateBook book) {
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
                                bool_or(status = 'Accepted') AS isBorrowed
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
                        AND b.deleted = false    
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

    public List<Book> getUserLikedBooks(String userId) {
        return jdbcClient.sql("""
                        WITH liked_books AS (
                            SELECT book_id
                            FROM book_share.book_like
                            WHERE user_id = :userId::uuid
                            ),
                        comments AS (
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
                                bool_or(status = 'Accepted') AS isBorrowed
                            FROM book_share.book_rent_request br
                            GROUP BY book_id
                        )
                        SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                               b.created_at AS dateAdded, b.genre, 
                               COALESCE(c.comments, '{}') AS comments, 
                               COALESCE(l.likes_number, 0) AS likesNumber, 
                               COALESCE(ib.isBorrowed, false) AS isBorrowed
                        FROM liked_books lb
                        JOIN book_share.book b ON lb.book_id = b.id
                        LEFT JOIN comments c ON lb.book_id = c.book_id
                        LEFT JOIN likes l ON lb.book_id = l.book_id
                        LEFT JOIN isBorrowed ib ON lb.book_id = ib.book_id
                        WHERE b.deleted = false    
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

    public List<Book> getUserBorrowedBooks(String userId) {
        return jdbcClient.sql("""
                        WITH borrowed_books AS (
                            SELECT br.book_id
                            FROM book_share.book_rent_request br
                            WHERE br.user_id = :userId::uuid
                            AND (status = 'Accepted')
                            ),
                        comments AS (
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
                                bool_or(status = 'Accepted') AS isBorrowed
                            FROM book_share.book_rent_request br
                            GROUP BY book_id
                        )
                        SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                               b.created_at AS dateAdded, b.genre, 
                               COALESCE(c.comments, '{}') AS comments, 
                               COALESCE(l.likes_number, 0) AS likesNumber, 
                               COALESCE(ib.isBorrowed, false) AS isBorrowed
                        FROM borrowed_books bb
                        JOIN book_share.book b ON bb.book_id = b.id
                        LEFT JOIN comments c ON bb.book_id = c.book_id
                        LEFT JOIN likes l ON bb.book_id = l.book_id
                        LEFT JOIN isBorrowed ib ON bb.book_id = ib.book_id
                        WHERE b.deleted = false    
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

    public List<Book> getUserReadBooks(String userId) {
        return jdbcClient.sql("""
                        WITH read_books AS (
                            SELECT DISTINCT (br.book_id)
                            FROM book_share.book_rent_request br
                            WHERE br.user_id = :userId::uuid
                            AND status = 'Returned'
                            ),
                        comments AS (
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
                                bool_or(status = 'Accepted') AS isBorrowed
                            FROM book_share.book_rent_request br
                            GROUP BY book_id
                        )
                        SELECT b.id, b.title, b.author, b.image_url AS imageUrl, b.description, 
                               b.created_at AS dateAdded, b.genre, 
                               COALESCE(c.comments, '{}') AS comments, 
                               COALESCE(l.likes_number, 0) AS likesNumber, 
                               COALESCE(ib.isBorrowed, false) AS isBorrowed
                        FROM read_books rb
                        JOIN book_share.book b ON rb.book_id = b.id
                        LEFT JOIN comments c ON rb.book_id = c.book_id
                        LEFT JOIN likes l ON rb.book_id = l.book_id
                        LEFT JOIN isBorrowed ib ON rb.book_id = ib.book_id
                        WHERE b.deleted = false    
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

    public void removeBook(String userId, Long bookId) {
        jdbcClient.sql("""
                            UPDATE book_share.book
                            SET deleted = true, deleted_at = NOW()      
                            WHERE user_id = :userId::uuid 
                            AND id = :bookId
                            AND NOT EXISTS (
                                SELECT 1
                                FROM book_share.book_rent_request br
                                WHERE br.book_id = :bookId 
                                AND br.status IN ('Pending', 'Accepted')
                            )
                        """)
                .param("userId", userId)
                .param("bookId", bookId)
                .update();
    }

    public Long borrowBook(String userId, Long bookId) {
        return jdbcClient.sql("""
                        INSERT INTO book_share.book_rent_request (user_id, book_id, status)
                        SELECT :userId::uuid, :bookId, 'Pending'
                        WHERE NOT EXISTS (
                            SELECT 1
                            FROM book_share.book_rent_request br
                            WHERE br.user_id = :userId::uuid 
                            AND br.book_id = :bookId 
                            AND br.status IN ('Pending', 'Accepted')
                        )
                        RETURNING id;
                        """)
                .param("userId", userId)
                .param("bookId", bookId)
                .query(Long.class)
                .single();
    }

    public Long returnBook(String userId, Long bookId) {
        return jdbcClient.sql("""
                        UPDATE book_share.book_rent_request br
                        SET status = 'Returned'
                        WHERE br.user_id = :userId::uuid
                        AND br.book_id = :bookId
                        AND br.status IN ('Accepted')
                        RETURNING id;
                        """)
                .param("userId", userId)
                .param("bookId", bookId)
                .query(Long.class)
                .single();
    }

    public Long updateBookRequest(
            String requestUserId,
            String ownerUserId,
            Long bookId,
            String expectedStatus,
            String status
    ) {
        return jdbcClient.sql("""
                        UPDATE book_share.book_rent_request br
                        SET status = :status
                        FROM book_share.book b
                        WHERE br.book_id = b.id
                          AND b.user_id = :ownerUserId::uuid
                          AND br.status = :expectedStatus
                          AND br.book_id = :bookId
                          AND br.user_id = :requestUserId::uuid
                        RETURNING br.id;
                            """)
                .param("requestUserId", requestUserId)
                .param("ownerUserId",ownerUserId)
                .param("bookId", bookId)
                .param("status", status)
                .param("expectedStatus",expectedStatus)
                .query(Long.class)
                .single();
    }


    public List<BookBorrowRequest> checkPendingRequests(String userId) {
        return jdbcClient.sql("""
                        SELECT br.id, br.book_id AS bookId, br.user_id AS userId, br.status, br.message, b.image_url, u.username
                        FROM book_share.book_rent_request br
                        JOIN book_share.book b ON br.book_id = b.id
                        JOIN book_share.user u ON br.user_id = u.id
                        WHERE b.user_id = :userId::uuid
                        AND br.status = 'Pending'
                        """)
                .param("userId", userId)
                .query(BookBorrowRequest.class)
                .list();
    }
}
