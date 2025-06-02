CREATE SCHEMA book_share;

CREATE DOMAIN book_share.genre_value AS VARCHAR(50)
    CHECK (VALUE IN ('Fiction','NonFiction', 'ScienceFiction', 'Science', 'History', 'Biography', 'Fantasy', 'Mystery', 'Romance', 'Undefined', 'Tech', 'Thriller', 'Horror'));

CREATE TABLE IF NOT EXISTS book_share.user (
    id UUID DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_pk PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS book_share.book (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    genre book_share.genre_value NOT NULL,
    image_url VARCHAR(255),
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    CONSTRAINT book_user_fk FOREIGN KEY (user_id) REFERENCES book_share.user(id),
    CONSTRAINT book_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS book_share.book_like (
    user_id UUID NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT book_like_user_fk FOREIGN KEY (user_id) REFERENCES book_share.user(id),
    CONSTRAINT book_like_book_fk FOREIGN KEY (book_id) REFERENCES book_share.book(id),
    CONSTRAINT book_like_pk PRIMARY KEY (user_id, book_id)
);

CREATE TABLE IF NOT EXISTS book_share.comment (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    book_id BIGINT NOT NULL,
    user_id UUID NOT NULL,
    content VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT comment_user_fk FOREIGN KEY (user_id) REFERENCES book_share.user(id),
    CONSTRAINT comment_book_fk FOREIGN KEY (book_id) REFERENCES book_share.book(id),
    CONSTRAINT comment_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS book_share.book_rent_request (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    book_id BIGINT NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('Pending', 'Accepted', 'Rejected', 'Done')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES book_share.book(id),
    FOREIGN KEY (user_id) REFERENCES book_share.user(id),
    CONSTRAINT book_rent_request_pk PRIMARY KEY (id)
);