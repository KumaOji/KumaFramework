package com.kuma.cloud.lab.javacore.config;

import com.kuma.cloud.lab.javacore.support.FileLabSupport;
import com.kuma.cloud.lab.javacore.support.SocketLabSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaCoreLabConfiguration {

    @Bean(destroyMethod = "close")
    SocketLabSupport socketLabSupport(JavaCoreLabProperties properties) {
        return new SocketLabSupport(properties.getSocketPort(), properties.getSocketTimeoutMillis());
    }

    @Bean
    FileLabSupport fileLabSupport(JavaCoreLabProperties properties) {
        return new FileLabSupport(properties.getFileWorkspace());
    }

}
