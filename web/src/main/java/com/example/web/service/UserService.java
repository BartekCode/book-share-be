package com.example.web.service;

import com.example.core.services.log.LogExecutionTime;
import com.example.db.model.Book;
import com.example.db.model.UserBookData;
import com.example.db.repository.book.BookRepository;
import com.example.db.repository.user.UserRepository;
import com.example.web.model.user.request.UserLoginRequest;
import com.example.web.model.user.request.UserRegisterRequest;
import com.example.web.model.user.response.UserDataResponse;
import com.example.web.model.user.response.UserRegisterResponse;
import com.example.web.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.web.utils.BookUtils.getBookDTOS;
import static com.example.web.utils.BookUtils.getUserBooksDTO;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();
    private  UserRepository userRepository;
    private BookRepository bookRepository;

    @Autowired
    public UserService(UserRepository userRepository, BookRepository bookRepository
    ) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
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

    @LogExecutionTime
    public UserDataResponse getUserData(UserLoginRequest userLoginRequest) {
        String password = userRepository.getUserPasswordByUsername(userLoginRequest.username());

        if (passwordEncoder.matches(
                userLoginRequest.password(),
                password
        )) {
            UserBookData userBookData = userRepository.getUserDataByName(userLoginRequest.username());
            return new UserDataResponse(
                    userBookData.id(),
                    userBookData.username(),
                    userBookData.email(),
                    bookRepository.getUserBooks(userBookData.id()).stream().map(Book::id).toList(),
                    bookRepository.getUserBorrowedBooks(userBookData.id()),
                    bookRepository.getUserReadBooks(userBookData.id()),
                    bookRepository.getUserLikedBooks(userBookData.id())
                    );
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
