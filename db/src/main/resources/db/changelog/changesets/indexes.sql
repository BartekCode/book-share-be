CREATE INDEX IF NOT EXISTS idx_book_user_id ON book_share.book(user_id);
CREATE INDEX IF NOT EXISTS idx_book_like_book_id ON book_share.book_like(book_id);
CREATE INDEX IF NOT EXISTS idx_book_like_user_id ON book_share.book_like(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_book_id ON book_share.comment(book_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON book_share.comment(user_id);
CREATE INDEX IF NOT EXISTS idx_book_rent_request_book_id ON book_share.book_rent_request(book_id);
CREATE INDEX IF NOT EXISTS idx_book_rent_request_user_id ON book_share.book_rent_request(user_id);