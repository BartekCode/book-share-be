package com.example.core.config;

import com.example.core.services.encoder.PasswordEncoderService;
import com.example.core.services.log.LogExecutionTimeAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({LogExecutionTimeAspect.class, PasswordEncoderService.class})
public class CoreConfig {
}
