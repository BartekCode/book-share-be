package com.example.web.service.user;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.book.Book;
import com.example.db.model.user.User;
import com.example.db.dao.book.BookDao;
import com.example.db.dao.user.UserDao;
import com.example.web.model.common.enums.Genre;
import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.model.user.dto.request.UserLoginRequest;
import com.example.web.model.user.dto.request.UserRegisterRequest;
import com.example.web.model.user.dto.response.UserDataResponse;
import com.example.web.model.user.dto.response.UserRegisterResponse;
import com.example.web.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();
    private UserDao userDao;
    private BookDao bookDao;
    private TransactionTemplate tx;

    @Autowired
    public UserService(
            UserDao userDao,
            BookDao bookDao,
            TransactionTemplate tx
    ) {
        this.userDao = userDao;
        this.bookDao = bookDao;
        this.tx = tx;
    }

    public UserService() {
    }

    @LogExecutionTime
    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) {
        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());
        return new UserRegisterResponse(userDao.registerUser(
                userRegisterRequest.username(),
                encodedPassword,
                userRegisterRequest.email()
        ));
    }

    @LogExecutionTime
    public UserDataResponse getLoggedUserData(UserLoginRequest userLoginRequest) {
        return tx.execute(status -> {
            User user = userDao.getUserDataByName(userLoginRequest.username());
            if (passwordEncoder.matches(
                    userLoginRequest.password(),
                    user.password()
            )) {
                return new UserDataResponse(
                        user.id(),
                        user.username(),
                        user.email(),
                        mapBooks(bookDao.getUserBooks(user.id())),
                        mapBooks(bookDao.getUserBorrowedBooks(user.id())),
                        mapBooks(bookDao.getUserReadBooks(user.id())),
                        mapBooks(bookDao.getUserLikedBooks(user.id())));
            } else {
                throw new IllegalArgumentException("Invalid username or password");
            }
        });
    }

    private List<BookResponse> mapBooks(List<Book> books) {
        return books.stream()
                .map(book -> new BookResponse(
                        book.id(),
                        book.title(),
                        book.author(),
                        book.imageUrl(),
                        book.description(),
                        book.dateAdded(),
                        Genre.fromCode(book.genre()),
                        book.comments(),
                        book.likesNumber(),
                        book.isBorrowed()
                ))
                .toList();
    }
}
