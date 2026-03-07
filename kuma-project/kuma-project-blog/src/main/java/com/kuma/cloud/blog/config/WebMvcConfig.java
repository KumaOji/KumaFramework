package com.kuma.cloud.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置，用于流式播放等场景的异步超时
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.mvc.async.request-timeout:600000}")
    private long asyncRequestTimeout;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(asyncRequestTimeout);
    }
}
