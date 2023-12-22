package com.practice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 설정 클래스
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // view에서 접근할 경로
    private String resourcePath = "/upload/**";
    // 실제 파일 저장 경로
    //private String savePath = "file:///C:/temp_springboot_img/";
    private String savePath = "file:///Users/hanabbun/app/temp_springboot_img/";
    // 파일 저장 경로 : /Users/hanabbun/app/temp_springboot_img/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);
    }
}
