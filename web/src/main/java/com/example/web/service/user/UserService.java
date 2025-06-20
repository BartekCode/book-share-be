package com.example.web.service.user;

import com.example.core.model.email.EmailTemplateName;
import com.example.core.model.role.RoleName;
import com.example.core.model.user.User;
import com.example.core.services.email.EmailService;
import com.example.core.services.encoder.PasswordEncoderService;
import com.example.core.services.log.LogExecutionTime;
import com.example.db.dao.book.BookDao;
import com.example.db.dao.role.RoleDao;
import com.example.db.dao.token.TokenDao;
import com.example.db.dao.token.TokenData;
import com.example.db.dao.user.UserDao;
import com.example.db.model.book.Book;
import com.example.db.model.token.Token;
import com.example.security.service.JwtService;
import com.example.web.model.book.dto.response.BookResponse;
import com.example.web.model.common.enums.Genre;
import com.example.web.model.user.dto.request.UserLoginRequest;
import com.example.web.model.user.dto.request.UserRegisterRequest;
import com.example.web.model.user.dto.response.UserDataResponse;
import com.example.web.model.user.dto.response.UserLoginResponse;
import com.example.web.model.user.dto.response.UserRegisterResponse;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    @Value("${mail.activationUrl}")
    private String activation_url;
    @Value("${security.activationCodeExpireTime}")
    private int activationCodeExpireTime;
    private PasswordEncoderService passwordEncoder;
    private EmailService emailService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UserDao userDao;
    private RoleDao roleDao;
    private TokenDao tokenDao;
    private BookDao bookDao;
    private TransactionTemplate tx;

    @Autowired
    public UserService(
            PasswordEncoderService passwordEncoder,
            EmailService emailService,
            AuthenticationManager authenticationManager, JwtService jwtService,
            UserDao userDao,
            RoleDao roleDao,
            TokenDao tokenDao,
            BookDao bookDao,
            TransactionTemplate tx
    ) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userDao = userDao;
        this.authenticationManager = authenticationManager;
        this.roleDao = roleDao;
        this.tokenDao = tokenDao;
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
                sendValidationEmail(userRegisterRequest.email(), userRegisterRequest.username(), registerResponse.userId());
            } catch (MessagingException e) {
                status.setRollbackOnly();
                throw new RuntimeException("Failed to send email", e);
            }
            return registerResponse;
        });
    }

    @LogExecutionTime
    public UserLoginResponse authenticateUser(UserLoginRequest userLoginRequest) {
        String jwtToken;
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequest.username(),
                            userLoginRequest.password()
                    )
            );
            HashMap<String, Object> claims = new HashMap<>();
            // Bo user implementuje Principal dlateog mozemy sie tak do niego dostac
            User user = (User) authenticate.getPrincipal();
            claims.put("username", user.username());
            claims.put("userId", user.id());
            // czyli nasz token bedzie zawierac username i userId
            jwtToken = jwtService.generateToken(claims, user);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return new UserLoginResponse(jwtToken);
    }

    @LogExecutionTime
    public void activateUserAccount(String token, String username) throws MessagingException {
        TokenData savedToken = tokenDao.retrieveToken(token, username)
                .orElseThrow(() -> new RuntimeException("No token found!"));
        if (LocalDateTime.now().isAfter(savedToken.expiresAt()) && !savedToken.enabled()){
            sendValidationEmail(savedToken.email(), savedToken.username(), savedToken.userId());
            throw new RuntimeException("Activation token has expired. A new token has been sent");
        }
        userDao.enableAccount(savedToken.userId(), savedToken.username());
    }

    private void sendValidationEmail(String email, String username, String userId) throws MessagingException {
        String newToken = generateAndSaveActivationToken(userId);
        String sanitizedEmail = email.trim().replaceAll("\\s+", "");
        emailService.sendEmail(
                sanitizedEmail,
                username,
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activation_url,
                newToken,
                "Aktywacja konta"
        );
    }

    private String generateAndSaveActivationToken(String userId) {
        String activationCode = generateActivationCode();
        Token token = new Token(activationCode, LocalDateTime.now().plusMinutes(activationCodeExpireTime));
        tokenDao.insertToken(userId, token);
        return activationCode;
    }

    @LogExecutionTime
    public UserDataResponse getLoggedUserData(String username, String password) {
        return tx.execute(status -> {
            User user = userDao.getUserDataByName(username);
            if (passwordEncoder.matches(
                    password,
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
