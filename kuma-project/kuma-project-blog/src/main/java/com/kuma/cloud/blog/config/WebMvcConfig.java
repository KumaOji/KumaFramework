package com.kuma.cloud.blog.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.context.event.EventListener;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置，用于流式播放等场景的异步超时
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Environment environment;
    private final ObjectProvider<RequestMappingHandlerAdapter> adapters;

    public WebMvcConfig(Environment environment, ObjectProvider<RequestMappingHandlerAdapter> adapters) {
        this.environment = environment;
        this.adapters = adapters;
    }

    private long asyncRequestTimeout() {
        return environment.getProperty("spring.mvc.async.request-timeout", Long.class, 600000L);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(asyncRequestTimeout());
    }

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void refreshAsyncTimeout() {
        adapters.orderedStream().forEach(adapter -> adapter.setAsyncRequestTimeout(asyncRequestTimeout()));
    }
}
