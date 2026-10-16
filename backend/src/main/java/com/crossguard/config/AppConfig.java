package com.crossguard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean public WebMvcConfigurer corsConfigurer(@Value("${crossguard.cors-origins}") String originList) {
        String[] origins = java.util.Arrays.stream(originList.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toArray(String[]::new);
        return new WebMvcConfigurer() {
            @Override public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins(origins).allowedMethods("GET", "POST", "PATCH", "OPTIONS").allowedHeaders("Authorization", "Content-Type").maxAge(3600);
            }
        };
    }
}
