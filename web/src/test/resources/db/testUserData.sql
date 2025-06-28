INSERT INTO book_share.user (username, password, email, account_locked, enabled)
VALUES ('testuser', 'f8a16311fa22d80ec1b19c829f7a84acfe6322e502eef863aad7833f2d04ad85b88fe8f6b49be283aca70ecbe81ff0b1', 'yrdy@gmail.com',  false, true);
INSERT INTO book_share.user_role (user_id, role_value)
VALUES ((SELECT id FROM book_share.user WHERE username = 'testuser'), 'User');
INSERT INTO book_share.book (title, author, genre, image_url, description, user_id)
VALUES ('Test Book', 'Test Author', 'Fiction', 'http://example.com/image.jpg', 'This is a test book description.', (SELECT id FROM book_share.user WHERE username = 'testuser'));
INSERT INTO book_share.book_like (user_id, book_id)
VALUES ((SELECT id FROM book_share.user WHERE username = 'testuser'), (SELECT id FROM book_share.book WHERE title = 'Test Book'));
INSERT INTO book_share.comment (book_id, user_id, content)
VALUES ((SELECT id FROM book_share.book WHERE title = 'Test Book'), (SELECT id FROM book_share.user WHERE username = 'testuser'), 'This is a test comment.');
INSERT INTO book_share.book_rent_request (book_id, user_id, status)
VALUES ((SELECT id FROM book_share.book WHERE title = 'Test Book'), (SELECT id FROM book_share.user WHERE username = 'testuser'), 'Returned');
