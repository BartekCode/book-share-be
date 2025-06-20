package com.example.app;

import com.example.core.config.CoreConfig;
import com.example.web.configuration.WebConfig;
import com.example.security.configuration.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@Import({
        WebConfig.class,
        CoreConfig.class,
        SecurityConfig.class
})
@EnableAspectJAutoProxy
@EnableAsync
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
