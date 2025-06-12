ALTER TABLE book_share.book_rent_request
    ADD COLUMN IF NOT EXISTS message VARCHAR(255) DEFAULT null;