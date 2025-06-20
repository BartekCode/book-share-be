package com.example.core.services.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderService implements PasswordEncoder {

    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder =
            Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Override
    public String encode(CharSequence rawPassword) {
        return pbkdf2PasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return pbkdf2PasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
