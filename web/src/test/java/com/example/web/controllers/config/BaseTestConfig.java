package com.example.web.controllers.config;

import com.example.core.config.CoreConfig;
import com.example.db.configuration.DbConfig;
import com.example.security.configuration.SecurityConfig;
import com.example.web.configuration.WebConfig;
import io.restassured.RestAssured;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Properties;

import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                DbConfig.class,
                CoreConfig.class,
                WebConfig.class,
                TestDbConfig.class,
                SecurityConfig.class
        }
)
@ActiveProfiles("test")
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureEmbeddedDatabase(
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD
)
@MockitoSpyBean(types = JavaMailSender.class)
public abstract class BaseTestConfig {

        @Autowired
        private JavaMailSender javaMailSender;

        @LocalServerPort
        private int port;

        @BeforeEach
        public void setup() {
                RestAssured.port = port;
                when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
        }
}
