package com.example.web.service;

import com.example.db.model.UserBookData;
import com.example.db.repository.book.BookRepository;
import com.example.db.repository.user.UserRepository;
import com.example.web.model.user.request.UserRegisterRequest;
import com.example.web.model.user.response.UserDataResponse;
import com.example.web.model.user.response.UserRegisterResponse;
import com.example.web.utils.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.web.utils.BookUtils.getBookDTOS;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public UserService(BookRepository bookRepository, UserRepository userRepository
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) {
        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());
        return new UserRegisterResponse(userRepository.registerUser(
                userRegisterRequest.username(),
                encodedPassword,
                userRegisterRequest.email()
        ));
    }

    public UserDataResponse getUserData(String username, String password) {
        UserBookData userBookData = bookRepository.getUserDataByNameAndPassword(username, password);
        return new UserDataResponse(
                userBookData.id(),
                getBookDTOS(userBookData.userBooks()),
                getBookDTOS(userBookData.borrowedBooks()),
                getBookDTOS(userBookData.likedBooks()
                ));
    }
}
