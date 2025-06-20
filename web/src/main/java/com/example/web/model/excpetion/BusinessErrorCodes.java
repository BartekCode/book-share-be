package com.example.web.model.excpetion;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {
    NO_CODE(0, "No code", NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Current password is incorrect", BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, "New password does not match", BAD_REQUEST),
    ACCOUNT_LOCKED(302, "User account locked", FORBIDDEN),
    ACCOUNT_DISABLED(303, "User account disabled", FORBIDDEN),
    BAD_CREDENTIALS(304, "Login and / or password is incorrect", FORBIDDEN),
    EMAIL_NOT_SENT(305, "Error occurred during sending an email", INTERNAL_SERVER_ERROR),
    INVALID_PARAMETERS(306, "Invalid parameters provided", INTERNAL_SERVER_ERROR),
    ;
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
