package com.cms.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow CORS for all paths and all origins
                registry.addMapping("/**")
                        .allowedOrigins("*") // allow any origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // allow all HTTP methods
                        .allowedHeaders("*") // allow any header
                        .allowCredentials(false) // if true, cannot use "*"
                        .maxAge(3600);
            }
        };
    }
}
