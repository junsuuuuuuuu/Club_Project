package com.JoinUs.dp.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves uploaded club images from the local filesystem under /static/uploads/**.
 * The upload directory is managed by ClubService.
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map HTTP requests to the local uploads directory (outside classpath)
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR)
                .setCachePeriod(0);
    }
}
