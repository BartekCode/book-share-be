package com.example.security.configuration;

import com.example.core.services.encoder.PasswordEncoderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class BeansConfig {

    private final PasswordEncoderService passwordEncoderService;
    private final UserDetailsService userDetailsService;

    public BeansConfig(PasswordEncoderService passwordEncoderService, UserDetailsService userDetailsService) {
        this.passwordEncoderService = passwordEncoderService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoderService);
        return authenticationProvider;
    }
}
