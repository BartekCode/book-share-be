package com.example.web.configuration;

import com.example.db.configuration.DbConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.example.web")
@Import(DbConfig.class)
public class WebConfig {

}
