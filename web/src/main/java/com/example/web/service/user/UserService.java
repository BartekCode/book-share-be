package com.example.web.service.user;

import com.example.core.model.email.EmailTemplateName;
import com.example.core.model.role.RoleName;
import com.example.core.model.user.User;
import com.example.core.services.email.EmailService;
import com.example.core.services.log.LogExecutionTime;
import com.example.db.dao.role.RoleDao;
import com.example.db.dao.token.TokenDao;
import com.example.db.model.book.Book;
import com.example.db.dao.book.BookDao;
import com.example.db.dao.user.UserDao;
import com.example.db.model.token.Token;
import com.example.web.model.common.enums.Genre;
import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.model.user.dto.request.UserLoginRequest;
import com.example.web.model.user.dto.request.UserRegisterRequest;
import com.example.web.model.user.dto.response.UserDataResponse;
import com.example.web.model.user.dto.response.UserRegisterResponse;
import com.example.core.services.encoder.PasswordEncoderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Value("${security.mail.activationCodeExpireTime}")
    private static final String activation_url = "/activate";
    @Value("${security.activationCodeExpireTime}")
    private int activationCodeExpireTime;
    private PasswordEncoderService passwordEncoder;
    private EmailService emailService;
    private UserDao userDao;
    private RoleDao roleDao;
    private TokenDao tokenDao;
    private BookDao bookDao;
    private TransactionTemplate tx;

    @Autowired
    public UserService(
            PasswordEncoderService passwordEncoder,
            UserDao userDao,
            RoleDao roleDao,
            BookDao bookDao,
            TransactionTemplate tx
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.bookDao = bookDao;
        this.tx = tx;
    }

    public UserService() {
    }

    @LogExecutionTime
    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) {
        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());

        return tx.execute(status -> {
            UserRegisterResponse registerResponse = new UserRegisterResponse(userDao.registerUser(
                    userRegisterRequest.username(),
                    encodedPassword,
                    userRegisterRequest.email()
            ));
            roleDao.insertRole(registerResponse.userId(), RoleName.USER);

            try {
                sendValidationEmail(userRegisterRequest, registerResponse.userId());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return registerResponse;
        });
    }

    private void sendValidationEmail(UserRegisterRequest user, String userId) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user.email(), userId);
        emailService.sendEmail(
                user.email(),
                user.username(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activation_url,
                newToken,
                "Aktywacja konta"
                );
    }

    private String generateAndSaveActivationToken(String email, String userId) {
        String activationCode = generateActivationCode();
        Token token = new Token(activationCode, LocalDateTime.now().plusMinutes(activationCodeExpireTime));
        tokenDao.insertToken(userId, token);
        return activationCode;
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

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
