package com.example.dealsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Handle favicon requests to prevent 404 errors
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }
}

