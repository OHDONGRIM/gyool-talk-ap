package com.gyooltalk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ DB에 저장된 절대 경로를 /images/profile/로 접근 가능하도록 설정
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
