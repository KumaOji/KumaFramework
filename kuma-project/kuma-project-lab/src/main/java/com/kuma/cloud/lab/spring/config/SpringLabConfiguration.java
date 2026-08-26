package com.kuma.cloud.lab.spring.config;

import com.kuma.cloud.lab.spring.ioc.JsonMessageRenderer;
import com.kuma.cloud.lab.spring.ioc.MessageRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 演示 Java Config 方式注册 Bean，与组件扫描形成对照。
 */
@Configuration
public class SpringLabConfiguration {

    @Bean
    @Primary
    MessageRenderer primaryMessageRenderer() {
        return new JsonMessageRenderer();
    }

}
