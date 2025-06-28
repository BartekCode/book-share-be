package com.example.web.controllers;

import com.example.db.exceptions.DbException;
import com.example.web.model.excpetion.BusinessErrorCodes;
import com.example.web.model.excpetion.ExceptionResponse;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.ACCOUNT_LOCKED.getCode(),
                        BusinessErrorCodes.ACCOUNT_LOCKED.getDescription(),
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.ACCOUNT_DISABLED.getCode(),
                        BusinessErrorCodes.ACCOUNT_DISABLED.getDescription(),
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.BAD_CREDENTIALS.getCode(),
                        BusinessErrorCodes.BAD_CREDENTIALS.getDescription(),
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.EMAIL_NOT_SENT.getCode(),
                        BusinessErrorCodes.EMAIL_NOT_SENT.getDescription(),
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException ex) {
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String errorMsg = error.getDefaultMessage();
                    errors.add(errorMsg);
                });
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.INVALID_PARAMETERS.getCode(),
                        BusinessErrorCodes.INVALID_PARAMETERS.getDescription(),
                        ex.getMessage(),
                        errors,
                        Map.of()
                ));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ExceptionResponse> handleException(ServletRequestBindingException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.INVALID_PARAMETERS.getCode(),
                        BusinessErrorCodes.INVALID_PARAMETERS.getDescription(),
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        999,
                        "Internal error, contact the admin",
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }

    @ExceptionHandler(DbException.class)
    public ResponseEntity<ExceptionResponse> handleException(DbException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        BusinessErrorCodes.DB_EXCEPTION.getCode(),
                        ex.getBusinessDesc(),
                        ex.getMessage(),
                        Set.of(),
                        Map.of()
                ));
    }
}
