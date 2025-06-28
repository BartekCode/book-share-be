package com.example.security.configuration;

import com.example.core.services.encoder.PasswordEncoderService;
import com.example.security.filter.JwtAuthenticationFilter;
import com.example.security.service.JwtService;
import com.example.security.service.SecurityService;
import com.example.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Import({SecurityService.class, JwtAuthenticationFilter.class, JwtService.class, UserDetailsServiceImpl.class})
public class SecurityConfig {

    private String feAppUrl;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private PasswordEncoderService passwordEncoderService;
    private UserDetailsService userDetailsService;

    public SecurityConfig() {
    }

    @Autowired
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            PasswordEncoderService passwordEncoderService,
            UserDetailsService userDetailsService,
            @Value("${security.frontend.url}") String feAppUrl
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoderService = passwordEncoderService;
        this.userDetailsService = userDetailsService;
        this.feAppUrl = feAppUrl;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoderService);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(feAppUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // wymieniamy urle bez autoryzacji czyli pobranie ksiazek i login
                .authorizeHttpRequests(req -> req.requestMatchers(
                                        "/v1/book/all",
                                        "/v1/user/register",
                                        "/v1/user/login",
                                        "/v1/user/confirm"
                                )
                                .permitAll()
                                // pozostale autoryzuj
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
