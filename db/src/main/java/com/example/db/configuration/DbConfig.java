package com.example.db.configuration;

import com.example.db.repository.book.BookRepository;
import com.example.db.repository.user.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({UserRepository.class, BookRepository.class})
public class DbConfig {
}
