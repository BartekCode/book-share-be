package com.example.web.controllers.config;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Consumer;

@Configuration
public class TestDbConfig {

    @Bean
    public Consumer<EmbeddedPostgres.Builder> embeddedPostgresCustomizer() {
        return builder -> builder.setPGStartupWait(Duration.ofSeconds(60L));
    }
}

