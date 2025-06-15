package com.example.db.configuration;

import com.example.db.repository.book.BookDao;
import com.example.db.repository.user.UserDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({UserDao.class, BookDao.class})
public class DbConfig {
}
