CREATE DOMAIN book_share.role AS VARCHAR(20)
    CHECK (VALUE IN ('User', 'Admin'));

CREATE TABLE IF NOT EXISTS book_share.user_role (
    user_id UUID NOT NULL,
    role_value book_share.role,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_role_user_fk FOREIGN KEY (user_id) REFERENCES book_share.user(id),
    CONSTRAINT user_role_pk PRIMARY KEY (user_id, role_value)
    );

CREATE TABLE IF NOT EXISTS book_share.token (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id UUID NOT NULL,
    token TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT token_user_fk FOREIGN KEY (user_id) REFERENCES book_share.user(id),
    CONSTRAINT token_pk PRIMARY KEY (id)
    );

ALTER TABLE book_share.user
    ADD COLUMN IF NOT EXISTS account_locked boolean DEFAULT false,
    ADD COLUMN IF NOT EXISTS enabled boolean DEFAULT false,
    ADD COLUMN IF NOT EXISTS last_modified_date timestamp;