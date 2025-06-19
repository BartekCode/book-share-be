package com.example.db.configuration;

import com.example.db.dao.book.BookDao;
import com.example.db.dao.like.LikeDao;
import com.example.db.dao.role.RoleDao;
import com.example.db.dao.token.TokenDao;
import com.example.db.dao.user.UserDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({UserDao.class, BookDao.class, LikeDao.class, RoleDao.class, TokenDao.class})
public class DbConfig {

}
