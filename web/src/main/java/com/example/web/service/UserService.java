package com.example.web.service;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.Book;
import com.example.db.model.UserData;
import com.example.db.repository.book.BookRepository;
import com.example.db.repository.user.UserRepository;
import com.example.web.model.Genre;
import com.example.web.model.book.BookDTO;
import com.example.web.model.user.request.UserLoginRequest;
import com.example.web.model.user.request.UserRegisterRequest;
import com.example.web.model.user.response.UserDataResponse;
import com.example.web.model.user.response.UserRegisterResponse;
import com.example.web.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private TransactionTemplate tx;

    @Autowired
    public UserService(
            UserRepository userRepository,
            BookRepository bookRepository,
            TransactionTemplate tx
    ) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.tx = tx;
    }

    public UserService() {
    }

    @LogExecutionTime
    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) {
        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());
        return new UserRegisterResponse(userRepository.registerUser(
                userRegisterRequest.username(),
                encodedPassword,
                userRegisterRequest.email()
        ));
    }

    //TODO   Przerobic by byly juz zwrcone ksiazki usera jako obiekty json a nie id
    @LogExecutionTime
    public UserDataResponse getUserData(UserLoginRequest userLoginRequest) {
        return tx.execute(status -> {
            UserData userData = userRepository.getUserDataByName(userLoginRequest.username());
            if (passwordEncoder.matches(
                    userLoginRequest.password(),
                    userData.password()
            )) {
                return new UserDataResponse(
                        userData.id(),
                        userData.username(),
                        userData.email(),
                        mapBooks(bookRepository.getUserBooks(userData.id())),
                        mapBooks(bookRepository.getUserBorrowedBooks(userData.id())),
                        mapBooks(bookRepository.getUserReadBooks(userData.id())),
                        mapBooks(bookRepository.getUserLikedBooks(userData.id())));
            } else {
                throw new IllegalArgumentException("Invalid username or password");
            }
        });
    }

    private List<BookDTO> mapBooks(List<Book> books) {
        return books.stream()
                .map(book -> new BookDTO(
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
