package com.JoinUs.dp.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")

                // ⭐ 모든 Origin 허용 (Swagger, localhost, 프론트 배포 주소 모두 허용)
                //   allowedOrigins("*")는 Credentials 허용 시 사용할 수 없음 → patterns 사용 필요
                .allowedOriginPatterns("*")

                // 허용할 HTTP 메서드
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

                // 모든 헤더 허용
                .allowedHeaders("*")

                // 쿠키·JWT(AUTHORIZATION) 등 인증정보 허용
                .allowCredentials(true)

                // Preflight 캐싱
                .maxAge(3600);
    }
}
