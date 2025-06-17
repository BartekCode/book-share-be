package com.example.db.dao.like;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class LikeDao {

    private final JdbcClient jdbcClient;

    public LikeDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void saveBookLike(String userId, Long bookId){
        jdbcClient.sql("""
                    INSERT INTO book_share.book_like (user_id, book_id) 
                    VALUES (:userId::uuid, :bookId)
                    ON CONFLICT (user_id, book_id) DO NOTHING;
                """)
                .param("userId", userId)
                .param("bookId", bookId)
                .update();
    }


    public void deleteBookLike(String userId, Long bookId){
        jdbcClient.sql("""
                    DELETE FROM book_share.book_like bl
                    WHERE :userId::uuid = bl.user_id
                    AND :bookId = bl.book_id
                """)
                .param("userId", userId)
                .param("bookId", bookId)
                .update();
    }
}
