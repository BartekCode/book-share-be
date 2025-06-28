INSERT INTO book_share.user (username, password, email, account_locked, enabled)
VALUES ('testuser', 'f8a16311fa22d80ec1b19c829f7a84acfe6322e502eef863aad7833f2d04ad85b88fe8f6b49be283aca70ecbe81ff0b1', 'yrdy@gmail.com',  false, true);
INSERT INTO book_share.user_role (user_id, role_value)
VALUES ((SELECT id FROM book_share.user WHERE username = 'testuser'), 'User');
