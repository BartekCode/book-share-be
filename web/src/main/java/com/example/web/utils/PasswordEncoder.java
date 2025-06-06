package com.example.web.utils;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class PasswordEncoder {

    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public String encode(String password) {
        return pbkdf2PasswordEncoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return pbkdf2PasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
