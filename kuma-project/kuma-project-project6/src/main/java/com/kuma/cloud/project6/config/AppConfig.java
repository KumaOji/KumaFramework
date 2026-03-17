package com.kuma.cloud.project6.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置
 */
@Configuration
public class AppConfig {

    /**
     * 幂等模块异常事件处理线程池
     */
    @Bean
    public ExecutorService idempotentExceptionEventExecutor() {
        return new ThreadPoolExecutor(
                2, 5, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                r -> new Thread(r, "idempotent-exception-" + r.hashCode()),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
